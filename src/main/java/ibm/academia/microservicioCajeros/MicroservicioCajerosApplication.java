package ibm.academia.microservicioCajeros;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class MicroservicioCajerosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioCajerosApplication.class, args);
	}

	@GetMapping
	public String requestData(@RequestBody Localizacion localizacion){
		String url  = "https://www.banamex.com/localizador/jsonP/json5.json";

		RestTemplate restTemplate =  new RestTemplate();
		String result = restTemplate.getForObject(url, String.class);

		String resultJson = result.substring(13,result.length()-2);

		JsonObject obj = new JsonParser().parse(resultJson).getAsJsonObject();

        JsonObject sucursales  = obj.get("Servicios").getAsJsonObject().get("100").getAsJsonObject().get("0").getAsJsonObject();
        JsonObject cajeros  = obj.get("Servicios").getAsJsonObject().get("300").getAsJsonObject().get("0").getAsJsonObject();

    

		return cajeros.toString() ;
    }

    private ArrayList<String> encuentraCajeros(JsonObject obj, Localizacion localizacion){
		ArrayList<String> list = new ArrayList<String>();
        obj.getAsJsonArray().forEach(element -> 
		Iterator itr = element.getAsJsonArray().iterator();
		while(itr.hasNext()){
			String s = (String)itr.next();
			if(s == localizacion.getDelegacion()){
				list.add(element);
			}
		}
		return null;
		);
		return list;

    }

}
