package com.nox.menu.modules.combat;

import com.nox.menu.core.Category;
import com.nox.menu.core.Module;
import com.nox.menu.core.setting.BooleanSetting;
import com.nox.menu.core.setting.NumberSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * CrystalAura v4 - REESCRITO DESDE CERO para ser lineal y predecible.
 *
 * Por qué v3 se comportaba raro ("a veces mira al target sin hacer nada"):
 * tenía demasiados estados con reintentos cruzados (PLACE->VERIFY->registerFailure->
 * PLACE otra vez) y recalculaba el "mejor" target cada 3 ticks, así que si había dos
 * enemigos a distancia parecida el bot flipeaba de uno a otro y recalculaba la
 * posición de bloque constantemente, dando la sensación de que se quedaba parado
 * "pensando". Aquí el target se FIJA en cuanto se elige y no cambia hasta que muere
 * o desaparece — cero flip-flopping.
 *
 * Por qué volvía al bloque de cristales sin enemigo: resetState() ponía isMoving=false
 * pero nunca ponía la velocidad real a 0, así que el impulso físico del tick anterior
 * te seguía empujando un instante hacia el último punto de spam. Ahora, en cuanto no
 * hay target, se fuerza velocidad horizontal a 0 inmediatamente y se devuelve control
 * total (cero rotación, cero movimiento) hasta que aparece un enemigo nuevo.
 *
 * v4.1 - fix "anchor fantasma": si el enemigo se aleja mucho del bloque fijo
 * (empujón, teletransporte, vuelo con elytra...) sin morir, el anchor quedaba
 * "atascado" en la posición vieja porque nada lo invalidaba salvo muerte,
 * bloque destruido o auto-stuck del propio jugador. Ahora se comprueba drift
 * de distancia contra el target en cada tick del bucle de cristal y al entrar
 * en PLACE_OBSIDIAN, y además se detecta vuelo sostenido (elytra) para dejar
 * de perseguir un punto en el aire y esperar a que aterrice antes de recalcular.
 *
 * FLUJO (lineal, sin bifurcaciones raras):
 *   1. Aparece un enemigo válido -> se fija como target.
 *   2. Si tiene ender pearl Y está lejos -> la lanza una vez.
 *   3. Se acerca caminando hasta estar en rango de colocar.
 *   4. Coloca obsidiana en un punto A 1-2 BLOQUES del enemigo (no pegado/debajo suyo).
 *   5. Golpea al enemigo UNA vez cuerpo a cuerpo.
 *   6. Bucle de cristal sobre ESE bloque fijo: coloca cristal -> detona -> coloca ->
 *      detona... a ritmo de 1 acción/tick (máximo que permite Minecraft), sin delays
 *      artificiales ni esperas de "verificación".
 *   7. Si el enemigo se aleja demasiado del anchor (sin volar) -> se suelta el
 *      anchor y se vuelve a APPROACH para recalcular sobre su posición actual.
 *   8. Si el enemigo lleva volando (elytra) más de un umbral -> se para todo
 *      (cero movimiento, cero spam) y se espera a que aterrice para recalcular.
 *   9. Enemigo muere -> parón total, control 100% al jugador, hasta el siguiente target.
 */
public class CrystalAura extends Module {

    // Rango y colocación
    private final NumberSetting range = new NumberSetting("Engage Range", 8.0, 3.0, 20.0, 0.5);
    private final NumberSetting placementOffset = new NumberSetting("Placement Offset", 2.0, 1.0, 2.0, 1.0);
    private final NumberSetting crystalInterval = new NumberSetting("Crystal Interval (ms)", 0.0, 0.0, 300.0, 5.0);

    // NUEVO: tolerancia de "drift" del anchor respecto al target antes de abandonarlo
    private final NumberSetting anchorDriftTolerance = new NumberSetting("Anchor Drift Tolerance", 3.0, 1.0, 10.0, 0.5);
    // NUEVO: ticks en el aire (elytra/cohetes) antes de parar y esperar aterrizaje (20 ticks = 1s)
    private final NumberSetting airborneAbortTicks = new NumberSetting("Airborne Abort Ticks", 60.0, 20.0, 200.0, 10.0);
    // NUEVO: distancia máxima a la que se permite FIJAR un target nuevo. Antes se
    // calculaba con pearlMinDistance*3 / range*3 (podía llegar a 27-30 bloques),
    // enganchando enemigos que, si había un obstáculo de por medio, eran
    // prácticamente inalcanzables -> quedarse caminando/mirando sin hacer nada.
    private final NumberSetting maxEngageDistance = new NumberSetting("Max Engage Distance", 20.0, 5.0, 60.0, 1.0);
    // NUEVO: si llevas más de este tiempo en APPROACH sin lograr colocar la primera
    // obsidiana (terreno imposible, obstáculo sin ruta directa...), se abandona el
    // target en vez de perseguirlo indefinidamente. 200 ticks ≈ 10s.
    private final NumberSetting approachTimeoutTicks = new NumberSetting("Approach Timeout (ticks)", 200.0, 40.0, 600.0, 20.0);

    // Targets
    private final BooleanSetting targetPlayers = new BooleanSetting("Target Players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Target Mobs", false);
    private final BooleanSetting targetAnimals = new BooleanSetting("Target Animals", false);
    private final BooleanSetting targetHostile = new BooleanSetting("Target Hostile", true);

    // Movimiento
    private final BooleanSetting autoMove = new BooleanSetting("Auto Move", true);
    private final NumberSetting moveSpeed = new NumberSetting("Move Speed", 0.3, 0.1, 0.5, 0.05);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump (anti-stuck)", true);

    // Pearl (opener, no fallback)
    private final BooleanSetting autoPearl = new BooleanSetting("Throw Pearl On Engage", true);
    private final NumberSetting pearlMinDistance = new NumberSetting("Pearl Min Distance", 10.0, 4.0, 40.0, 1.0);

    // Cámara (solo cosmético, nunca bloquea acciones)
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation Ease", 0.4, 0.05, 0.9, 0.05);
    private final NumberSetting rotationJitter = new NumberSetting("Rotation Jitter", 1.0, 0.0, 10.0, 0.5);
    private final BooleanSetting smoothCamera = new BooleanSetting("Smooth Camera", true);
    

    private enum State {
        IDLE,
        THROW_PEARL,
        APPROACH,
        PLACE_OBSIDIAN,
        PUNCH,
        CRYSTAL_LOOP
    }

    private State currentState = State.IDLE;
    private LivingEntity target = null;   // FIJO una vez elegido, no se recalcula "el mejor" a mitad de combate
    private BlockPos anchorBlockPos = null; // el bloque de obsidiana donde se spamea el cristal
    private long lastActionTime = 0;
    private long stateStartTime = 0;
    private boolean pearlThrownForTarget = false;
    private final Random random = new Random();

    // --- Rotación visual (cosmética) ---
    private float targetYaw = 0;
    private float targetPitch = 0;
    private boolean hasRotationGoal = false;
    private int tickCounter = 0;

    // --- Movimiento / anti-stuck ---
    private boolean isMoving = false;
    private Vec3 lastSelfPos = null;
    private int selfStuckTicks = 0;
    private int jumpCooldown = 0;

    // --- NUEVO: seguimiento de vuelo sostenido del target (elytra/cohetes) ---
    private int targetAirborneTicks = 0;

    public CrystalAura() {
        super("CrystalAura", "Crystal PvP automatico: obsidiana, cristal y golpe | Auto Crystal PvP: obsidian, crystal and hit", Category.COMBAT);

        
        this.addSetting(this.range);
        this.addSetting(this.placementOffset);
        this.addSetting(this.crystalInterval);
        this.addSetting(this.anchorDriftTolerance);
        this.addSetting(this.airborneAbortTicks);
        this.addSetting(this.maxEngageDistance);
        this.addSetting(this.approachTimeoutTicks);

        this.addSetting(this.targetPlayers);
        this.addSetting(this.targetMobs);
        this.addSetting(this.targetAnimals);
        this.addSetting(this.targetHostile);

        this.addSetting(this.autoMove);
        this.addSetting(this.moveSpeed);
        this.addSetting(this.autoJump);

        this.addSetting(this.autoPearl);
        this.addSetting(this.pearlMinDistance);

        this.addSetting(this.rotationSpeed);
        this.addSetting(this.rotationJitter);
        this.addSetting(this.smoothCamera);
        
    }

    @Override
    public void onEnable() {
        hardStop();
        
    }

    // ---------- Control de estado ----------

    /** Corte total: sin target, sin movimiento, sin rotación forzada. Control 100% al jugador. */
    private void hardStop() {
        currentState = State.IDLE;
        target = null;
        anchorBlockPos = null;
        pearlThrownForTarget = false;
        selfStuckTicks = 0;
        lastSelfPos = null;
        targetAirborneTicks = 0;
        if (mc.player != null) {
            isMoving = false;
            // Fix del bug real: v3 solo ponía isMoving=false sin tocar la velocidad,
            // así que el impulso físico del tick anterior seguía arrastrando al jugador
            // hacia el último bloque de spam. Aquí se anula de verdad.
            mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
        }
    }

    private void setState(State newState) {
        currentState = newState;
        stateStartTime = System.currentTimeMillis();
        lastActionTime = System.currentTimeMillis();
    }

    @Override
    public void onTick() {
        tickCounter++;
        if (!this.nullCheck() || mc.player == null || mc.level == null) return;

        // Si el target actual murió/desapareció -> corte total inmediato, control al jugador
        if (target != null && (!target.isAlive() || target.isRemoved() || target.getHealth() <= 0)) {
            hardStop();
        }

        // Solo se busca un target nuevo cuando NO hay uno ya fijado (evita el flip-flop
        // entre enemigos parecidos que causaba el "se queda mirando sin hacer nada")
        if (target == null) {
            findAndLockTarget();
            if (target == null) {
                // Nada que atacar: cero rotación, cero movimiento, control total al jugador.
                return;
            }
            setState(State.THROW_PEARL);
        }

        double distance = mc.player.distanceTo(target);
        updateSelfStuckDetection();

        // NUEVO: detección de vuelo sostenido del target (elytra, cohetes de fuego,
        // levitación...). Si lleva demasiado tiempo sin tocar suelo, perseguir su
        // posición o el anchor viejo es inútil: se suelta el anchor, se para todo
        // movimiento/rotación de acción y se espera a que aterrice para recalcular
        // sobre su posición real en el suelo.
        if (!target.onGround()) {
            targetAirborneTicks++;
        } else {
            targetAirborneTicks = 0;
        }

        if (targetAirborneTicks > airborneAbortTicks.getValue()) {
            if (anchorBlockPos != null) {
                anchorBlockPos = null;
            }
            if (currentState != State.APPROACH) {
                setState(State.APPROACH);
            }
            stopMoving();
            rotateToward(new Vec3(target.getX(), target.getY(), target.getZ()));
            return; // no ejecuta el switch de estados mientras esté volando
        }

        switch (currentState) {
            case IDLE:
                setState(State.THROW_PEARL);
                break;
            case THROW_PEARL:
                handleThrowPearl(distance);
                break;
            case APPROACH:
                handleApproach(distance);
                break;
            case PLACE_OBSIDIAN:
                handlePlaceObsidian(distance);
                break;
            case PUNCH:
                handlePunch();
                break;
            case CRYSTAL_LOOP:
                handleCrystalLoop();
                break;
        }

        if (autoMove.isEnabled()) {
            autoMoveForState();
        }
        applySmoothRotation();
    }

    // ---------- Estados ----------

    private void handleThrowPearl(double distance) {
        if (autoPearl.isEnabled() && !pearlThrownForTarget
                && distance > pearlMinDistance.getValue() && hasItem(Items.ENDER_PEARL)) {
            Vec3 throwPos = new Vec3(
                target.getX() + (random.nextDouble() - 0.5) * 2,
                target.getY() + 1.0,
                target.getZ() + (random.nextDouble() - 0.5) * 2
            );
            boolean[] thrown = {false};
            faceAndAct(throwPos, () -> thrown[0] = throwPearl());
            if (thrown[0]) {
                pearlThrownForTarget = true;
                setState(State.APPROACH);
            }
            // si falla (no tiene pearl en la ranura por lo que sea), simplemente se
            // reintenta el siguiente tick; no hay bucles de fallo complicados.
        } else {
            pearlThrownForTarget = true;
            setState(State.APPROACH);
        }
    }

    private void handleApproach(double distance) {
        // NUEVO: si llevas demasiado tiempo intentando llegar a rango sin conseguirlo
        // (terreno imposible, obstáculo sin ruta directa, target huyendo constantemente...),
        // abandona el target en vez de quedarte caminando/mirando hacia él para siempre.
        // stateStartTime se fija en setState(), así que mide el tiempo desde que
        // ENTRASTE en APPROACH, no desde que se fijó el target.
        long elapsedInApproach = System.currentTimeMillis() - stateStartTime;
        if (elapsedInApproach > approachTimeoutTicks.getValue() * 50L) {
            hardStop();
            return;
        }

        if (distance <= range.getValue()) {
            findOffsetPlacementPosition();
            if (anchorBlockPos != null) {
                setState(State.PLACE_OBSIDIAN);
                return;
            }
        }
        rotateToward(new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ()));
    }

    private void handlePlaceObsidian(double distance) {
        // NUEVO: además de comprobar que el bloque siga siendo físicamente válido,
        // comprobamos que no se haya alejado demasiado del target (drift).
        if (anchorBlockPos == null || !isPositionValid(anchorBlockPos) || !anchorStillValidForTarget()) {
            findOffsetPlacementPosition();
        }
        if (anchorBlockPos == null) {
            setState(State.APPROACH);
            return;
        }
        if (!hasItem(Items.OBSIDIAN)) {
            // Sin obsidiana no hay nada que hacer: nos quedamos quietos mirando al
            // enemigo en vez de spamear intentos fallidos.
            rotateToward(new Vec3(target.getX(), target.getY(), target.getZ()));
            return;
        }

        double distToBlock = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()).distanceTo(Vec3.atCenterOf(anchorBlockPos));
        if (distToBlock > range.getValue() + 1) {
            setState(State.APPROACH);
            return;
        }

        rotateToward(new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.5, target.getZ()));

        Vec3 aimPos = Vec3.atCenterOf(anchorBlockPos.below()).add(0, 0.5, 0);
        boolean[] placed = {false};
        faceAndAct(aimPos, () -> placed[0] = placeObsidian());
        if (placed[0]) {
            setState(State.PUNCH);
        }
    }

    private void handlePunch() {
        if (anchorBlockPos == null) {
            // Guarda de seguridad: si el anchor se soltó justo antes de entrar aquí
            // (drift/airborne), evitamos NPE y replanteamos desde APPROACH.
            setState(State.APPROACH);
            return;
        }
        if (!(mc.level.getBlockState(anchorBlockPos).getBlock() == net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
            // La obsidiana no llegó a colocarse de verdad (server la rechazó): reintenta.
            setState(State.PLACE_OBSIDIAN);
            return;
        }
        faceAndAct(new Vec3(target.getX(), target.getY() + target.getEyeHeight() * 0.6, target.getZ()), () -> {
            mc.gameMode.attack(mc.player, target);
            mc.player.swing(InteractionHand.MAIN_HAND);
        });
        setState(State.CRYSTAL_LOOP);
    }

    /**
     * Bucle de cristal a máxima velocidad sobre el bloque fijo (anchorBlockPos).
     * Nada de estados VERIFY intermedios ni delays artificiales salvo crystalInterval
     * (0 en Rage/Turbo): cada tick comprueba el estado real del bloque y actúa.
     * Si el enemigo muere, el chequeo de arriba en onTick corta todo antes de llegar aquí.
     * NUEVO: si el enemigo se alejó demasiado del anchor (drift) sin que ninguna otra
     * condición lo detectara, se abandona el anchor y se vuelve a APPROACH en vez de
     * seguir colocando/detonando sobre un punto ya vacío.
     */
    private void handleCrystalLoop() {
        if (anchorBlockPos == null) {
            setState(State.APPROACH);
            return;
        }

        // NUEVO: comprobación de drift. Cubre el caso de knockback fuerte, teleport,
        // etc. donde el enemigo sigue en el suelo pero ya lejos del bloque fijo.
        if (!anchorStillValidForTarget()) {
            anchorBlockPos = null;
            setState(State.APPROACH);
            return;
        }

        // Si por lo que sea la obsidiana desapareció (rota por el rival, etc.), se
        // repone antes de seguir. Esto SÍ puede pasar en combate real.
        if (!(mc.level.getBlockState(anchorBlockPos).getBlock() == net.minecraft.world.level.block.Blocks.OBSIDIAN)) {
            if (hasItem(Items.OBSIDIAN)) {
                Vec3 aimPos = Vec3.atCenterOf(anchorBlockPos.below()).add(0, 0.5, 0);
                faceAndAct(aimPos, this::placeObsidianOnAnchor);
            }
            return;
        }

        long interval = crystalInterval.getValue().longValue();
        if (System.currentTimeMillis() - lastActionTime < interval) return;

        EndCrystal existingCrystal = findCrystalAt(anchorBlockPos);
        if (existingCrystal != null) {
            faceAndAct(new Vec3(existingCrystal.getX(), existingCrystal.getY(), existingCrystal.getZ()), () -> {
                mc.gameMode.attack(mc.player, existingCrystal);
                mc.player.swing(InteractionHand.MAIN_HAND);
            });
        } else if (hasItem(Items.END_CRYSTAL)) {
            Vec3 aimPos = Vec3.atCenterOf(anchorBlockPos).add(0, 0.5, 0);
            faceAndAct(aimPos, this::placeCrystalOnAnchor);
        }
        lastActionTime = System.currentTimeMillis();
    }

    // ---------- Movimiento ----------

    private void autoMoveForState() {
        if (target == null || mc.player == null) return;

        Vec3 moveGoal;
        boolean shouldMove;

        switch (currentState) {
            case APPROACH:
                moveGoal = new Vec3(target.getX(), target.getY(), target.getZ());
                shouldMove = mc.player.distanceTo(target) > range.getValue() * 0.7;
                break;
            case PLACE_OBSIDIAN:
                if (anchorBlockPos == null) return;
                moveGoal = Vec3.atCenterOf(anchorBlockPos);
                shouldMove = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()).distanceTo(moveGoal) > range.getValue() * 0.6;
                break;
            default:
                // THROW_PEARL, PUNCH, CRYSTAL_LOOP: quietos en el sitio, plantados.
                stopMoving();
                return;
        }

        if (!shouldMove) {
            stopMoving();
            return;
        }

        Vec3 playerPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        double dx = moveGoal.x - playerPos.x;
        double dz = moveGoal.z - playerPos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist < 0.5) {
            stopMoving();
            return;
        }

        double moveX = (dx / dist) * moveSpeed.getValue();
        double moveZ = (dz / dist) * moveSpeed.getValue();
        mc.player.setDeltaMovement(moveX, mc.player.getDeltaMovement().y, moveZ);
        isMoving = true;
    }

    private void stopMoving() {
        if (isMoving && mc.player != null) {
            isMoving = false;
            mc.player.setDeltaMovement(0, mc.player.getDeltaMovement().y, 0);
        }
    }

    private void updateSelfStuckDetection() {
        if (jumpCooldown > 0) jumpCooldown--;

        Vec3 currentPos = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (lastSelfPos != null && isMoving) {
            double moved = currentPos.distanceTo(lastSelfPos);
            if (moved < 0.03 && mc.player.horizontalCollision) {
                selfStuckTicks++;
            } else {
                selfStuckTicks = 0;
            }
        } else {
            selfStuckTicks = 0;
        }
        lastSelfPos = currentPos;

        if (selfStuckTicks > 4 && autoJump.isEnabled() && jumpCooldown == 0 && mc.player.onGround()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.42, mc.player.getDeltaMovement().z);
            jumpCooldown = 10;
        }

        if (selfStuckTicks > 30) {
            // Atasco real y persistente: abandona el punto de colocación actual y
            // recalcula desde cero en vez de insistir contra la misma pared.
            anchorBlockPos = null;
            selfStuckTicks = 0;
            if (currentState == State.PLACE_OBSIDIAN) {
                setState(State.APPROACH);
            } else if (currentState == State.APPROACH) {
                // NUEVO: antes esto no hacía nada si ya estabas en APPROACH (que es
                // justo el caso de "target lejos"), así que un atasco contra una
                // pared/obstáculo mientras te acercabas te dejaba plantado rotando
                // hacia el enemigo para siempre. Ahora se abandona el target del
                // todo y se libera el control al jugador hasta el siguiente objetivo.
                hardStop();
            }
        }
    }

    // ---------- Targeting ----------

    /** Elige target SOLO si no hay uno ya fijado. Nunca cambia de objetivo a mitad de combate. */
    private void findAndLockTarget() {
        try {
            LivingEntity best = null;
            double bestDist = Double.MAX_VALUE;

            for (Entity e : mc.level.entitiesForRendering()) {
                if (!(e instanceof LivingEntity) || e == mc.player || e.isRemoved()) continue;
                LivingEntity le = (LivingEntity) e;
                if (!le.isAlive() || le.getHealth() <= 0) continue;
                if (!passesTargetFilter(e)) continue;

                Vec3 pos = new Vec3(e.getX(), e.getY(), e.getZ());
                if (!(pos.y > -70 && pos.y < 325)) continue;

                double dist = mc.player.distanceTo(e);
                // NUEVO: límite explícito en vez de la fórmula pearlMinDistance*3/range*3
                // (que con valores por defecto dejaba fijar enemigos a 27-30 bloques,
                // muchas veces inalcanzables si había un obstáculo de por medio).
                if (dist > maxEngageDistance.getValue()) continue;

                if (dist < bestDist) {
                    bestDist = dist;
                    best = le;
                }
            }
            target = best;
        } catch (Exception ex) {
            target = null;
        }
    }

    private boolean passesTargetFilter(Entity e) {
        if (e instanceof Player) {
            return targetPlayers.isEnabled();
        }
        if (!targetMobs.isEnabled()) return false;
        boolean isHostile = isHostileMob(e);
        return isHostile ? targetHostile.isEnabled() : targetAnimals.isEnabled();
    }

    private boolean isHostileMob(Entity entity) {
        String name = entity.getType().getDescription().getString().toLowerCase();
        String[] hostileMobs = {
            "zombie", "skeleton", "creeper", "spider", "witch", "enderman",
            "blaze", "ghast", "slime", "magma cube", "silverfish", "cave spider",
            "pillager", "vindicator", "evoker", "vex", "ravager", "warden",
            "piglin brute", "hoglin", "zoglin", "drowned", "husk", "stray",
            "phantom", "shulker", "guardian", "elder guardian"
        };
        for (String mob : hostileMobs) {
            if (name.contains(mob)) return true;
        }
        return false;
    }

    // ---------- Colocación ----------

    /**
     * NUEVO: comprueba si el anchor actual sigue "cerca" del target ahora mismo.
     * Umbral = placementOffset (distancia esperada al colocarlo) + tolerancia
     * configurable. Si el enemigo se aleja más de eso sin haber muerto ni haber
     * disparado la detección de vuelo, esto es lo que fuerza abandonar el anchor
     * y recalcular en APPROACH.
     */
    private boolean anchorStillValidForTarget() {
        if (anchorBlockPos == null || target == null) return false;
        double drift = Vec3.atCenterOf(anchorBlockPos).distanceTo(new Vec3(target.getX(), target.getY(), target.getZ()));
        return drift <= placementOffset.getValue() + anchorDriftTolerance.getValue();
    }

    /**
     * Busca un bloque a 1-2 (placementOffset) bloques del enemigo — NUNCA el bloque
     * pegado o debajo del propio enemigo — tal como se pidió. Escanea un anillo a esa
     * distancia horizontal y elige el más cercano al jugador para minimizar el
     * desplazamiento necesario.
     */
    private void findOffsetPlacementPosition() {
        if (target == null) return;
        anchorBlockPos = null;

        int offset = (int) Math.round(placementOffset.getValue());
        BlockPos targetPos = target.blockPosition();
        BlockPos best = null;
        double bestDistToPlayer = Double.MAX_VALUE;

        for (int x = -offset; x <= offset; x++) {
            for (int z = -offset; z <= offset; z++) {
                int chebyshev = Math.max(Math.abs(x), Math.abs(z));
                if (chebyshev != offset) continue; // solo el anillo exacto a esa distancia

                BlockPos candidate = targetPos.offset(x, 0, z);
                if (!mc.level.getBlockState(candidate).canBeReplaced()) continue;
                BlockPos below = candidate.below();
                if (!mc.level.getBlockState(below).isSolid()) continue;
                if (mc.level.getBlockState(below).getBlock() == net.minecraft.world.level.block.Blocks.BEDROCK) continue;

                double d = new Vec3(mc.player.getX(), mc.player.getY(), mc.player.getZ()).distanceTo(Vec3.atCenterOf(candidate));
                if (d < bestDistToPlayer) {
                    bestDistToPlayer = d;
                    best = candidate;
                }
            }
        }

        // Fallback: si no hay hueco válido en el anillo exacto, prueba a 1 bloque
        // (más flexible que fallar del todo).
        if (best == null && offset > 1) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    BlockPos candidate = targetPos.offset(x, 0, z);
                    if (!mc.level.getBlockState(candidate).canBeReplaced()) continue;
                    BlockPos below = candidate.below();
                    if (!mc.level.getBlockState(below).isSolid()) continue;
                    if (mc.level.getBlockState(below).getBlock() == net.minecraft.world.level.block.Blocks.BEDROCK) continue;
                    best = candidate;
                    break;
                }
                if (best != null) break;
            }
        }

        anchorBlockPos = best;
    }

    private boolean placeObsidian() {
        return placeObsidianAt(anchorBlockPos);
    }

    private void placeObsidianOnAnchor() {
        placeObsidianAt(anchorBlockPos);
    }

    private boolean placeObsidianAt(BlockPos pos) {
        if (pos == null) return false;
        int slot = findItemSlot(Items.OBSIDIAN);
        if (slot == -1) return false;

        int oldSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(slot));

        BlockHitResult hit = new BlockHitResult(
            new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
            Direction.UP, pos.below(), false
        );
        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(oldSlot));
        lastActionTime = System.currentTimeMillis();
        return true;
    }

    private void placeCrystalOnAnchor() {
        if (anchorBlockPos == null) return;
        int slot = findItemSlot(Items.END_CRYSTAL);
        if (slot == -1) return;

        BlockPos crystalPos = anchorBlockPos.above();
        if (!mc.level.getBlockState(crystalPos).canBeReplaced()) return;

        int oldSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(slot));

        BlockHitResult hit = new BlockHitResult(
            new Vec3(anchorBlockPos.getX() + 0.5, anchorBlockPos.getY() + 1.5, anchorBlockPos.getZ() + 0.5),
            Direction.UP, anchorBlockPos, false
        );
        mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(oldSlot));
    }

    private boolean throwPearl() {
        int slot = findItemSlot(Items.ENDER_PEARL);
        if (slot == -1) return false;
        int oldSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(slot));
        mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
        mc.player.connection.send(new ServerboundSetCarriedItemPacket(oldSlot));
        return true;
    }

    private EndCrystal findCrystalAt(BlockPos pos) {
        if (pos == null) return null;
        Vec3 center = Vec3.atCenterOf(pos.above());
        List<Entity> entities = new ArrayList<>();
        for (Entity e : mc.level.entitiesForRendering()) entities.add(e);

        return (EndCrystal) entities.stream()
                .filter(e -> e instanceof EndCrystal && e.isAlive())
                .filter(e -> new Vec3(e.getX(), e.getY(), e.getZ()).distanceTo(center) <= 1.5)
                .min(Comparator.comparingDouble(e -> new Vec3(e.getX(), e.getY(), e.getZ()).distanceTo(center)))
                .orElse(null);
    }

    // ---------- Rotación ----------

    /**
     * Rotación de ACCIÓN: paquete instantáneo con el ángulo real hacia aimPos justo
     * para ejecutar la acción (colocar/golpear), sin depender de que la cámara visual
     * haya convergido. Después restaura la rotación visual para no cortar el giro
     * suave que se ve en pantalla.
     */
    private void faceAndAct(Vec3 aimPos, Runnable action) {
        if (mc.player == null || mc.player.connection == null) return;

        Vec3 eyesPos = mc.player.getEyePosition();
        double dx = aimPos.x - eyesPos.x;
        double dy = aimPos.y - eyesPos.y;
        double dz = aimPos.z - eyesPos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float actionYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0f);
        float actionPitch = Mth.clamp((float) (-Math.toDegrees(Math.atan2(dy, dist))), -90, 90);

        float savedYaw = mc.player.getYRot();
        float savedPitch = mc.player.getXRot();

        mc.player.setYRot(actionYaw);
        mc.player.setXRot(actionPitch);
        mc.player.connection.send(
            new ServerboundMovePlayerPacket.Rot(actionYaw, actionPitch, mc.player.onGround(), false)
        );

        action.run();

        mc.player.setYRot(savedYaw);
        mc.player.setXRot(savedPitch);
        mc.player.connection.send(
            new ServerboundMovePlayerPacket.Rot(savedYaw, savedPitch, mc.player.onGround(), false)
        );
    }

    /** Solo cosmético: hacia dónde gira la cámara que VES. Nunca bloquea acciones. */
    private void rotateToward(Vec3 targetPos) {
        Vec3 eyesPos = mc.player.getEyePosition();
        double dx = targetPos.x - eyesPos.x;
        double dy = targetPos.y - eyesPos.y;
        double dz = targetPos.z - eyesPos.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float rawYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0f);
        float rawPitch = Mth.clamp((float) (-Math.toDegrees(Math.atan2(dy, dist))), -90, 90);

        float maxJitter = rotationJitter.getValue().floatValue();
        if (maxJitter > 0 && mc.player != null) {
            float currentDiff = Math.abs(Mth.wrapDegrees(rawYaw - mc.player.getYRot()));
            float decayFactor = Mth.clamp(currentDiff / 30f, 0f, 1f);
            rawYaw += (float) ((random.nextDouble() - 0.5) * maxJitter) * decayFactor;
            rawPitch += (float) ((random.nextDouble() - 0.5) * maxJitter * 0.5) * decayFactor;
        }

        targetYaw = rawYaw;
        targetPitch = Mth.clamp(rawPitch, -90, 90);
        hasRotationGoal = true;
    }

    private void applySmoothRotation() {
        if (mc.player == null || !hasRotationGoal) return;

        float currentYaw = mc.player.getYRot();
        float currentPitch = mc.player.getXRot();

        float yawDiff = Mth.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        float newYaw;
        float newPitch;

        if (smoothCamera.isEnabled()) {
            float ease = rotationSpeed.getValue().floatValue();
            float yawStep = Mth.clamp(yawDiff * ease, -25f, 25f);
            float pitchStep = Mth.clamp(pitchDiff * ease, -25f, 25f);
            if (Math.abs(yawDiff) < 0.05f) yawStep = yawDiff;
            if (Math.abs(pitchDiff) < 0.05f) pitchStep = pitchDiff;
            newYaw = currentYaw + yawStep;
            newPitch = Mth.clamp(currentPitch + pitchStep, -90, 90);
        } else {
            newYaw = targetYaw;
            newPitch = Mth.clamp(targetPitch, -90, 90);
        }

        mc.player.setYRot(newYaw);
        mc.player.setXRot(newPitch);
        if (mc.player.connection != null) {
            mc.player.connection.send(
                new ServerboundMovePlayerPacket.Rot(newYaw, newPitch, mc.player.onGround(), false)
            );
        }
    }

    // ---------- Utilidades ----------

    private boolean isPositionValid(BlockPos pos) {
        if (pos == null) return false;
        if (!mc.level.getBlockState(pos).canBeReplaced()) return false;
        if (!mc.level.getBlockState(pos.below()).isSolid()) return false;
        return mc.level.getBlockState(pos.below()).getBlock() != net.minecraft.world.level.block.Blocks.BEDROCK;
    }

    private boolean hasItem(net.minecraft.world.item.Item item) {
        return findItemSlot(item) != -1;
    }

    private int findItemSlot(net.minecraft.world.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getItem(i).getItem() == item) return i;
        }
        return -1;
    }

}

