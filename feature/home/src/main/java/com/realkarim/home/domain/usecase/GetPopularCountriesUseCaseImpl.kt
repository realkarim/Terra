package com.realkarim.home.domain.usecase

import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.home.domain.repository.HomeRepository
import com.realkarim.network.model.ErrorResponse
import javax.inject.Inject

class GetPopularCountriesUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository
) : GetPopularCountriesUseCase {

    override suspend fun invoke(): Outcome<List<Country>, ErrorResponse> {
        return homeRepository.getAllCountries()
    }
}