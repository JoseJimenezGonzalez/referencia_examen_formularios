package com.example.formularios

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.formularios.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var esEmailCorrecto = false
    var esPasswordCorrecto = false
    var esCodigoPostalCorrecto = false
    var esEdadValida = false

    var opcionSeleccionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextListeners()
        setupButtonListener()

        //Configurar el spinner
        //Obtenemos el spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
        //Creamos un array con el que vamos a inflar al spinner
        val opciones = arrayOf("Hombre", "Mujer", "Imbécil")
        // Crear un ArrayAdapter utilizando la lista de opciones y el diseño predeterminado
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)
        // Aplicar el adaptador al Spinner
        spinner.adapter = adapter

        // Configurar un listener para manejar la selección de elementos en el Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Obtener la opción seleccionada
                val selectedItem = opciones[position]

                // Asignar la opción seleccionada
                opcionSeleccionada = selectedItem

                // Mostrar la opción seleccionada
                Toast.makeText(this@MainActivity, "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }



    }

    private fun setupButtonListener() {
        binding.btnEnviar.setOnClickListener {
            //Variable que puede ser null o empty
            var codigoPostal = binding.tietCodigoPostal.text.toString()
            if(codigoPostal.isBlank()){
                esCodigoPostalCorrecto = true
            }
            //Tenemos que pasarnos a la otra actividad los datos
            if(!esCodigoPostalCorrecto){
                Toast.makeText(this, "Falla el puto codigo postal", Toast.LENGTH_SHORT).show()
            }
            if(esPasswordCorrecto && esEdadValida && esCodigoPostalCorrecto && esEmailCorrecto){
                //Cogemos lo que ponga en los edit text
                var correoElectronico = binding.tietCorreo.text.toString()
                var password = binding.tietPass.text.toString()

                //Pasamos a la siguiente actividad
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                //Solo vamos a pasar las dos anteriores a la otra actividad
                intent.putExtra("correoUsuario", correoElectronico)
                intent.putExtra("passUsuario", password)
                intent.putExtra("opcionSpinner", opcionSeleccionada)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Falla algo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTextListeners() {
        //Correo
        binding.tietCorreo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            //Este es el que vamos a modificar
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val texto: String = p0.toString()
                //Tenemos que comprobar que es válido, esto es que al menos debe de contener un . y una @
                validarEmail(texto)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //Contraseña
        binding.tietPass.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Este es el que me interesa
                val pass: String = p0.toString()
                validarPass(pass)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //Codigo postal
        binding.tietCodigoPostal.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val codPostal = p0.toString()
                validarCodigoPostal(codPostal)
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //Fecha de nacimiento
        binding.tietFechaNacimiento.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val selectedDate = Date(selectedDateInMillis)
                val currentDate = Date()

                if (isMayorDeEdad(selectedDate, currentDate)) {
                    // La persona es mayor de edad, realiza la acción correspondiente
                    val fechaNacimientoFormateada = obtenerFechaNacimientoFormateada(selectedDate)
                    binding.tietFechaNacimiento.setText(fechaNacimientoFormateada)
                    esEdadValida = true
                } else {
                    // La persona no es mayor de edad, puedes mostrar un mensaje o realizar otra acción
                    Toast.makeText(this, "Debes ser mayor de edad", Toast.LENGTH_SHORT).show()
                    esEdadValida = false
                }
            }

            picker.show(supportFragmentManager, "jeje")
        }
    }

    // Función para verificar si la persona es mayor de edad
    fun isMayorDeEdad(fechaNacimiento: Date, fechaActual: Date): Boolean {
        val edadMinima = 18 // La edad mínima para ser considerado mayor de edad

        val calendarNacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
        val calendarActual = Calendar.getInstance().apply { time = fechaActual }

        val aniosDiferencia = calendarActual.get(Calendar.YEAR) - calendarNacimiento.get(Calendar.YEAR)

        // Verificar si la persona tiene al menos la edad mínima
        return if (calendarActual.get(Calendar.DAY_OF_YEAR) < calendarNacimiento.get(Calendar.DAY_OF_YEAR)) {
            aniosDiferencia - 1 >= edadMinima
        } else {
            aniosDiferencia >= edadMinima
        }
    }

    // Función para obtener la fecha de nacimiento en formato yyyy/MM/dd
    fun obtenerFechaNacimientoFormateada(fechaNacimiento: Date): String {
        val formato = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return formato.format(fechaNacimiento)
    }

    private fun validarCodigoPostal(codPostal: String) {
        //Para que sea valido solo debe contener numeros y tener longitud exacta de 5
        var soloContieneNum: Boolean = false
        var tieneLongitudVal: Boolean = false
        fun contieneNumeros(codPostal: String){
            soloContieneNum = codPostal.all { it.isDigit() }
        }
        fun longitudValida(codPostal: String){
            tieneLongitudVal = codPostal.length == 5
        }
        contieneNumeros(codPostal)
        longitudValida(codPostal)
        if (soloContieneNum && tieneLongitudVal){
            binding.tilCodigoPostal.error = null //Borra el mensaje de error si lo hubiera de antes
            esCodigoPostalCorrecto = true
        }else if(codPostal.isBlank()){
            binding.tilCodigoPostal.error = null //Borra el mensaje de error si lo hubiera de antes
            esCodigoPostalCorrecto = true
        }else{
            esCodigoPostalCorrecto = false
            binding.tilCodigoPostal.error = "No es obligatorio pero si se introduce debe de ser válido"
        }
    }

    private fun validarPass(pass: String) {
        var contieneMayus: Boolean = false
        var contieneMinus: Boolean = false
        var tieneLongitudVal: Boolean = false
        var contieneNum: Boolean = false

        fun contieneMayuscula(cadena: String){
            contieneMayus = cadena.any(){it.isUpperCase()}
        }
        fun contieneMinuscula(cadena: String){
            contieneMinus = cadena.any(){it.isLowerCase()}
        }
        fun tieneLongitudValida(cadena: String){
            tieneLongitudVal = cadena.length > 7
        }
        fun contieneNumero(cadena: String){
            contieneNum = cadena.any(){it.isDigit()}
        }
        contieneMayuscula(pass)
        contieneMinuscula(pass)
        tieneLongitudValida(pass)
        contieneNumero(pass)
        if(contieneMayus && contieneMinus && tieneLongitudVal && contieneNum){
            binding.tilPass.error = null //Borra el mensaje de error si lo hubiera de antes
            esPasswordCorrecto = true
        }else{
            esPasswordCorrecto = false
            binding.tilPass.error = "Debe contener numero, letra mayus, letra minus y longitud mayor a 7 caracteres"
        }
    }

    private fun validarEmail(texto: String) {
        if(texto.contains(".") && texto.contains("@")){
            binding.tilCorreo.error = null //Borra el mensaje de error si lo hubiera de antes
            esEmailCorrecto = true
        }else{
            esEmailCorrecto = false
            binding.tilCorreo.error = "El correo debe de contener @ y ."
        }
    }
}