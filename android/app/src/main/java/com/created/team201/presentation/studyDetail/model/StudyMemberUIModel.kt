package com.created.team201.presentation.studyDetail.model

data class StudyMemberUIModel(
    val id: Long,
    val isMaster: Boolean,
    val isApplicant: Boolean,
    val profileImageUrl: String,
    val name: String,
    val successRate: Int,
    val tier: Int,
    val isDeleted: Boolean,
)
