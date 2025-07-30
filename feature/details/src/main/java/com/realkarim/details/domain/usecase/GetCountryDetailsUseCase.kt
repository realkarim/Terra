package com.realkarim.details.domain.usecase

import com.realkarim.domain.Outcome
import com.realkarim.domain.model.Country
import com.realkarim.network.model.ErrorResponse

interface GetCountryDetailsUseCase {
    suspend operator fun invoke(countryName: String): Outcome<Country, ErrorResponse>
}