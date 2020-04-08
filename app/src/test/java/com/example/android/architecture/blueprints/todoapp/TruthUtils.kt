package com.example.android.architecture.blueprints.todoapp

import com.google.common.truth.Subject
import com.google.common.truth.Truth.assertThat

infix fun Subject.equalTo(value: Any?) {
    isEqualTo(value)
}

infix fun Subject.notEqualTo(value: Any?) {
    isNotEqualTo(value)
}

infix fun Any.assert(value: Any): Subject {
    return assertThat(value)
}