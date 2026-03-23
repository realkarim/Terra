package com.realkarim.favourites.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result
import com.realkarim.favourites.repository.FavouritesRepository
import javax.inject.Inject

class ToggleFavouriteUseCaseImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepository
) : ToggleFavouriteUseCase {

    override suspend fun invoke(country: Country): Result<Unit, DomainError> {
        return favouritesRepository.toggleFavourite(country)
    }
}
