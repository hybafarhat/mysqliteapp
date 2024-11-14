package com.example.mysqliteapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sqliteHelper = SQLiteHelper(this)
        setContent {
            StudentApp()
        }
    }

    @Composable
    fun StudentApp() {
        var name by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var studentId by remember { mutableStateOf<Int?>(null) }
        var students by remember { mutableStateOf(listOf<Student>()) }

        // Charger les étudiants depuis la base de données
        LaunchedEffect(Unit) {
            students = sqliteHelper.getAllStudents()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = age,
                onValueChange = { newValue ->
                    // Permet uniquement les chiffres dans l'entrée d'âge
                    if (newValue.all { it.isDigit() }) {
                        age = newValue
                    }
                },
                label = { Text("Âge") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (name.isNotEmpty() && ageInt != null) {
                        val student = Student(name = name, age = ageInt)
                        val success = sqliteHelper.insertStudent(student)
                        if (success > 0) {
                            Toast.makeText(this@MainActivity, "Étudiant ajouté", Toast.LENGTH_SHORT).show()
                            name = ""
                            age = ""
                            students = sqliteHelper.getAllStudents() // Recharge les étudiants
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ajouter")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (name.isNotEmpty() && ageInt != null && studentId != null) {
                        val student = Student(id = studentId!!, name = name, age = ageInt)
                        val success = sqliteHelper.updateStudent(student)
                        if (success > 0) {
                            Toast.makeText(this@MainActivity, "Étudiant mis à jour", Toast.LENGTH_SHORT).show()
                            name = ""
                            age = ""
                            studentId = null
                            students = sqliteHelper.getAllStudents() // Recharge les étudiants
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modifier")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (studentId != null) {
                        val success = sqliteHelper.deleteStudent(studentId!!)
                        if (success > 0) {
                            Toast.makeText(this@MainActivity, "Étudiant supprimé", Toast.LENGTH_SHORT).show()
                            name = ""
                            age = ""
                            studentId = null
                            students = sqliteHelper.getAllStudents() // Recharge les étudiants
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Supprimer")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Afficher la liste des étudiants
            StudentList(students, onStudentClick = { student ->
                name = student.name
                age = student.age.toString()
                studentId = student.id
            })
        }
    }

    @Composable
    fun StudentList(students: List<Student>, onStudentClick: (Student) -> Unit) {
        Column {
            students.forEach { student ->
                StudentRow(student = student, onClick = onStudentClick)
            }
        }
    }

    @Composable
    fun StudentRow(student: Student, onClick: (Student) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick(student) },

        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(text = "Nom: ${student.name}")
                Text(text = "Âge: ${student.age}")
            }
        }
    }
}
