package com.realkarim.favourites.usecase

import com.realkarim.country.model.Country
import kotlinx.coroutines.flow.Flow

interface GetFavouriteCountriesUseCase {
    operator fun invoke(): Flow<List<Country>>
}
