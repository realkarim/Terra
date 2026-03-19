package com.realkarim.favourites.repository

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    suspend fun toggleFavourite(country: Country): Result<Unit, DomainError>
    fun observeAllFavourites(): Flow<List<Country>>
    fun observeIsFavourite(alphaCode: String): Flow<Boolean>
}
