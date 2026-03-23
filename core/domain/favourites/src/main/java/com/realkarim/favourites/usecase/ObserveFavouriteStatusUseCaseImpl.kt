package com.realkarim.favourites.usecase

import com.realkarim.favourites.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavouriteStatusUseCaseImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : ObserveFavouriteStatusUseCase {

    override fun invoke(alphaCode: String): Flow<Boolean> {
        return favouritesRepository.observeIsFavourite(alphaCode)
    }
}
