import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationTarget
import kotlinx.kover.api.VerificationValueType

plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    isDisabled.set(false)

    filters {
        classes {
            excludes += listOf("**.model.**")
        }
    }

    xmlReport {
        onCheck.set(false)
    }

    htmlReport {
        onCheck.set(true)
    }

    verify {
        onCheck.set(true)

        rule {
            name = "LineCoverage"
            isEnabled = false
            target = VerificationTarget.CLASS

            bound {
                minValue = 90
                counter = CounterType.LINE
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }

        rule {
            name = "BranchCoverage"
            isEnabled = false
            target = VerificationTarget.CLASS

            bound {
                minValue = 80
                counter = CounterType.BRANCH
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}
