package com.realkarim.home.data.mapper

import com.realkarim.home.data.model.CountryDto
import com.realkarim.home.data.model.CurrencyDto
import com.realkarim.home.data.model.LanguageDto
import com.realkarim.home.data.model.RegionalBlocDto
import com.realkarim.home.domain.model.Country
import com.realkarim.home.domain.model.Currency
import com.realkarim.home.domain.model.Language
import com.realkarim.home.domain.model.RegionalBloc

fun CountryDto.toDomain(): Country = Country(
    name = name.orEmpty(),
    callingCodes = callingCodes.orEmpty(),
    capital = capital.orEmpty(),
    subregion = subregion.orEmpty(),
    region = region.orEmpty(),
    population = population ?: 0L,
    area = area,
    timezones = timezones.orEmpty(),
    borders = borders.orEmpty(),
    nativeName = nativeName.orEmpty(),
    flagUrl = flags?.png.orEmpty(),
    currencies = currencies.orEmpty().map { it.toDomain() },
    languages = languages.orEmpty().map { it.toDomain() },
    regionalBlocs = regionalBlocs.orEmpty().map { it.toDomain() }
)

fun CurrencyDto.toDomain(): Currency = Currency(
    code = code.orEmpty(),
    name = name.orEmpty(),
    symbol = symbol.orEmpty()
)

fun LanguageDto.toDomain(): Language = Language(
    name = name.orEmpty(),
    nativeName = nativeName.orEmpty()
)

fun RegionalBlocDto.toDomain(): RegionalBloc = RegionalBloc(
    acronym = acronym.orEmpty(),
    name = name.orEmpty()
)
