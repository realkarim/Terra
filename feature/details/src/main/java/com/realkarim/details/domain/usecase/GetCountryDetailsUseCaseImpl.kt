package com.realkarim.details.domain.usecase

import com.realkarim.details.domain.repository.DetailsRepository
import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class GetCountryDetailsUseCaseImpl @Inject constructor(
    private val detailsRepository: DetailsRepository
) : GetCountryDetailsUseCase {

    override suspend fun invoke(countryName: String): Outcome<Country, ErrorResponse> {
        return detailsRepository.getCountryByName(countryName)
    }
}