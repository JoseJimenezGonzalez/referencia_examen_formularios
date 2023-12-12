package com.example.formularios

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.formularios.databinding.ActivityMain2Binding
import com.example.formularios.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        //Me traigo los datos de la otra actividad
        val correo = intent.getStringExtra("correoUsuario")
        val password = intent.getStringExtra("passUsuario")
        val opcionSpiner = intent.getStringExtra("opcionSpinner")

        //Los pongo en los text view de esta actividad
        binding.tvCorreo.text = "Correo del usuario: " + correo
        binding.tvPassword.text = "Contraseña del usuario: " + password
        binding.tvOpcionSpinner.text = "La opcion del spinner es: " + opcionSpiner
    }
}