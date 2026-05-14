package com.eggetteluo.todayclass.data.local

import com.eggetteluo.todayclass.data.local.entity.CourseTimeRuleEntity

object DefaultTimeRules {

    // 单号楼作息时间
    val oddBuildingRules = listOf(
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 1,
            startTime = "08:00",
            endTime = "08:45",
            remark = "第一节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 2,
            startTime = "08:55",
            endTime = "09:40",
            remark = "第二节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 3,
            startTime = "10:25",
            endTime = "11:10",
            remark = "第三节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 4,
            startTime = "11:20",
            endTime = "12:05",
            remark = "第四节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 5,
            startTime = "14:00",
            endTime = "14:45",
            remark = "第五节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 6,
            startTime = "14:55",
            endTime = "15:40",
            remark = "第六节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 7,
            startTime = "16:25",
            endTime = "17:10",
            remark = "第七节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 8,
            startTime = "17:20",
            endTime = "18:05",
            remark = "第八节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 9,
            startTime = "19:20",
            endTime = "20:05",
            remark = "第九节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "ODD",
            sectionNo = 10,
            startTime = "20:15",
            endTime = "21:00",
            remark = "第十节课"
        )
    )

    // 双号楼作息时间
    val evenBuildingRules = listOf(
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 1,
            startTime = "08:20",
            endTime = "09:05",
            remark = "第一节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 2,
            startTime = "09:15",
            endTime = "10:00",
            remark = "第二节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 3,
            startTime = "10:35",
            endTime = "11:20",
            remark = "第三节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 4,
            startTime = "11:30",
            endTime = "12:15",
            remark = "第四节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 5,
            startTime = "14:20",
            endTime = "15:05",
            remark = "第五节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 6,
            startTime = "15:15",
            endTime = "16:00",
            remark = "第六节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 7,
            startTime = "16:35",
            endTime = "17:20",
            remark = "第七节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 8,
            startTime = "17:30",
            endTime = "18:15",
            remark = "第八节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 9,
            startTime = "19:20",
            endTime = "20:05",
            remark = "第九节课"
        ),
        CourseTimeRuleEntity(
            buildingType = "EVEN",
            sectionNo = 10,
            startTime = "20:15",
            endTime = "21:00",
            remark = "第十节课"
        )
    )

    // 合并后的全部规则
    val allRules = oddBuildingRules + evenBuildingRules
}
