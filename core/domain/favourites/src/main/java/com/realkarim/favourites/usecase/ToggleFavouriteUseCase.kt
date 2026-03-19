package com.realkarim.favourites.usecase

import com.realkarim.country.model.Country
import com.realkarim.domain.error.DomainError
import com.realkarim.domain.result.Result

interface ToggleFavouriteUseCase {
    suspend operator fun invoke(country: Country): Result<Unit, DomainError>
}
