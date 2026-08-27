package com.nox.menu.mixin;

// StaticDrops fue movido a MixinItemEntityRenderer#redirectMultiply.
// EntityRenderState no tiene campos yaw/pitch en 1.21.5; la rotación
// se congela via @Redirect sobre MatrixStack.multiply en el render().
// Este archivo se mantiene vacío para no romper el build.

