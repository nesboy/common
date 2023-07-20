import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType
import kotlinx.kover.gradle.plugin.dsl.MetricType

plugins {
    id("org.jetbrains.kotlinx.kover")
}

koverReport {
    defaults {
        filters {
            excludes {
                classes("**.model.**")
                annotatedBy("IgnoreCoverage")
            }
        }

        xml {
            onCheck = false
        }

        html {
            onCheck = true
        }

        verify {
            onCheck = true

            rule("LineCoverage") {
                isEnabled = false
                entity = GroupingEntityType.CLASS

                bound {
                    minValue = 90
                    metric = MetricType.LINE
                    aggregation = AggregationType.COVERED_PERCENTAGE
                }
            }

            rule("BranchCoverage") {
                isEnabled = false
                entity = GroupingEntityType.CLASS

                bound {
                    minValue = 80
                    metric = MetricType.BRANCH
                    aggregation = AggregationType.COVERED_PERCENTAGE
                }
            }
        }
    }
}
