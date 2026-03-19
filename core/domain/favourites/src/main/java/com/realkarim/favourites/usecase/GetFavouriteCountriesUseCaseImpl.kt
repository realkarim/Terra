package com.realkarim.favourites.usecase

import com.realkarim.country.model.Country
import com.realkarim.favourites.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteCountriesUseCaseImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : GetFavouriteCountriesUseCase {

    override fun invoke(): Flow<List<Country>> {
        return favouritesRepository.observeAllFavourites()
    }
}
