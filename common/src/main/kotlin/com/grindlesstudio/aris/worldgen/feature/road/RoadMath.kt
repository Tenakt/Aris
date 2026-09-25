package com.grindlesstudio.aris.worldgen.feature.road

import kotlin.math.max
import kotlin.math.min

object RoadMath {

    fun distanceToSegmentSqr(
        px: Double,
        pz: Double,
        ax: Double,
        az: Double,
        bx: Double,
        bz: Double
    ): Double {
        val dx = bx - ax
        val dz = bz - az

        val lengthSqr = dx * dx + dz * dz

        if (lengthSqr == 0.0) {
            val x = px - ax
            val z = pz - az
            return x * x + z * z
        }

        var t = ((px - ax) * dx + (pz - az) * dz) / lengthSqr
        t = max(0.0, min(1.0, t))

        val projectedX = ax + t * dx
        val projectedZ = az + t * dz

        val x = px - projectedX
        val z = pz - projectedZ

        return x * x + z * z
    }

    fun distanceToSegment(
        px: Double,
        pz: Double,
        ax: Double,
        az: Double,
        bx: Double,
        bz: Double
    ): Double {
        return kotlin.math.sqrt(
            distanceToSegmentSqr(
                px,
                pz,
                ax,
                az,
                bx,
                bz
            )
        )
    }
}