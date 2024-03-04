package com.example.sbma_project.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.sbma_project.data.ILocationService
import com.example.sbma_project.data.LocationWithSpeed
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationService: ILocationService
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<LocationWithSpeed?> = locationService.requestLocationUpdates()

}