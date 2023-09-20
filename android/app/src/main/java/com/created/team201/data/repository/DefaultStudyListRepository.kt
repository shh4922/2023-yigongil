package com.created.team201.data.repository

import com.created.domain.model.StudySummary
import com.created.domain.repository.StudyListRepository
import com.created.team201.data.datasource.remote.StudyListDataSource
import com.created.team201.data.mapper.toDomain
import javax.inject.Inject

class DefaultStudyListRepository @Inject constructor(
    private val studyListDataSource: StudyListDataSource,
) : StudyListRepository {

    override suspend fun getStudyList(page: Int): List<StudySummary> {
        return studyListDataSource.getStudyList(page).toDomain()
    }

    override suspend fun getSearchedStudyList(searchWord: String, page: Int): List<StudySummary> {
        return studyListDataSource.getSearchedStudyList(searchWord, page).toDomain()
    }
}
