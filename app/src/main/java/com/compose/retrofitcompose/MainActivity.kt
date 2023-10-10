package com.compose.retrofitcompose

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.retrofitcompose.model.CryptoModel
import com.compose.retrofitcompose.service.CryptoAPI
import com.compose.retrofitcompose.ui.theme.RetrofitComposeTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetrofitComposeTheme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(){
    val cryptoModel= remember { mutableStateListOf<CryptoModel>() }
    val BASE_URL="https://raw.githubusercontent.com/"

    val retrofit=Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(CryptoAPI::class.java)

    val call=retrofit.getData()

    call.enqueue(object  : Callback<List<CryptoModel>>{
        override fun onResponse(
            call: Call<List<CryptoModel>>,
            response: Response<List<CryptoModel>>
        ) {
            if(response.isSuccessful){
                response.body()?.let {
                    //çekilen verileri bir remember aracılıgıyla mutableStateListOf a kayıt ediyorum
                        cryptoModel.addAll(it)
                }
            }
        }

        override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
            t.printStackTrace()
        }

    })
    //artık cryptoModel listesini burada verebilirim
    //Appbar bende çalışmadı ondan gizledim
    Scaffold(topBar = {AppBar()}) {
        CryptoList(cyrptos = cryptoModel)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(){
    TopAppBar(title = {
        Text(text = "Retrofit Compose", fontSize = 26.sp, color = Color.Red)},
        modifier = Modifier.padding(top = 0.dp, bottom = 40.dp))
}

//Verileri gösterirken Lazy Column kullanıyoruz.
// Çünkü her seferinde composable objesi oluşturmuyor recyclerview gibi çalışıyo
@Composable
fun CryptoList(cyrptos:List<CryptoModel>){
    LazyColumn(contentPadding = PaddingValues(25.dp)){
        items(cyrptos){
            //Tek bir text göstermek isteseydik Text yeterli olucaktı  bundan dolayı mutableList kullancaz.StateManagement yapısını
            CryptoRow(crypto = it)
        }
    }
}
@Composable
fun CryptoRow(crypto:CryptoModel){
    //genişliği fulluyor fillmaxwidth
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .fillMaxSize()){
        Text(text = crypto.currency,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 40.dp),
            fontWeight = FontWeight.Bold)
        Text(text = crypto.price)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RetrofitComposeTheme {
        CryptoRow(crypto = CryptoModel("ASDSDAd","ADSADSDASDD"))
    }
}