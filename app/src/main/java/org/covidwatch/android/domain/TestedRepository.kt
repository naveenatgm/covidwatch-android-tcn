package org.covidwatch.android.domain

interface TestedRepository {

    fun setUserTestedPositive()

    fun removeUserTestedPositive()

    fun isUserTestedPositive(): Boolean
}