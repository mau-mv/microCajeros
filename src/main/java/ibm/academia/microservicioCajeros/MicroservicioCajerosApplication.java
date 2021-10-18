package ibm.academia.microservicioCajeros;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public List<JsonNode> requestData(@RequestBody Localizacion localizacion){
		String url  = "https://www.banamex.com/localizador/jsonP/json5.json";

		RestTemplate restTemplate =  new RestTemplate();
		String result = restTemplate.getForObject(url, String.class);

		String json_str = result.substring(13,result.length()-2);

		ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode atmJsonNode = mapper.readTree(json_str).get("Servicios").get("300").get("0");
			JsonNode sucursalesJsonNode = mapper.readTree(json_str).get("Servicios").get("400").get("0");
			
            List<JsonNode> sucursalesAtms =  new ArrayList<JsonNode>();
			atmJsonNode.forEach(x -> sucursalesAtms.add(x));
			sucursalesJsonNode.forEach(x -> sucursalesAtms.add(x));

			List<JsonNode> resultJsonNodes =  new ArrayList<JsonNode>();

			for (final JsonNode objNode : sucursalesAtms) {
				Pattern p = Pattern.compile("(?<=C.P.(\s)?)\\d+");
				Matcher m = p.matcher(objNode.get(4).asText());

				if (m.find()) {
					if (localizacion.getCp().equalsIgnoreCase(m.group(0)) ||  localizacion.getDelegacion().equalsIgnoreCase(objNode.get(17).asText() )) {
						resultJsonNodes.add(objNode);
					}
				}
			}

			return resultJsonNodes;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

		return new ArrayList<JsonNode>();
    }

}
