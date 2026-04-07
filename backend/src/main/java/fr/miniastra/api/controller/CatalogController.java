package fr.miniastra.api.controller;

import fr.miniastra.domain.model.FreightModel;
import fr.miniastra.domain.model.PassengerModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @GetMapping("/passenger-models")
    public List<PassengerModel> passengerModels() {
        return PassengerModel.ALL;
    }

    @GetMapping("/freight-models")
    public List<FreightModel> freightModels() {
        return FreightModel.ALL;
    }
}
