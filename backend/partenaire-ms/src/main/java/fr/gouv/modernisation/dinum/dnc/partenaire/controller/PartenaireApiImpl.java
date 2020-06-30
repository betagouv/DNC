package fr.gouv.modernisation.dinum.dnc.partenaire.controller;

import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Credential;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Partenaire;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.Credentials;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.GrantAccessPartner;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.PartenaireInfos;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.server.PartenaireApi;
import fr.gouv.modernisation.dinum.dnc.partenaire.service.PartenaireService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller pour les endpoints /partenaire
 */
@RestController
public class PartenaireApiImpl implements PartenaireApi {

	@Autowired
	private PartenaireService partenaireService;

	@Override
	public ResponseEntity<GrantAccessPartner> checkCredentialsPartenaire(@NotNull @Valid String siretPartenaire, @NotNull @Valid String clientId, @NotNull @Valid String clientSecret) {
		Credential credential = partenaireService.checkCredentials(siretPartenaire, clientId, clientSecret);

		if(Objects.isNull(credential)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		return ResponseEntity.ok( new GrantAccessPartner() );
	}

	@Override
	public ResponseEntity<List<PartenaireInfos>> getListePartenaires() {
		List<Partenaire> partenaires = partenaireService.findAll();

		List<PartenaireInfos> results = new ArrayList<>(partenaires.stream().map(this::convertPartenaire).collect(Collectors.toList()));

		return ResponseEntity.ok(results);
	}

	@Override
	public ResponseEntity<PartenaireInfos> createPartenaire(@Valid PartenaireInfos partenaireInfos) {
		return ResponseEntity.ok(convertPartenaire(partenaireService.createPartenaire(partenaireInfos)));
	}

	private PartenaireInfos convertPartenaire(Partenaire partenaire) {
		PartenaireInfos partenaireInfos = new PartenaireInfos();
		BeanUtils.copyProperties(partenaire, partenaireInfos, "id");
		return partenaireInfos;
	}

	@Override
	public ResponseEntity<String> saveCredentials(String siretPartenaire, @Valid Credentials body) {
		// Les exceptions sont gérés par la couche de service
		Credential credential = partenaireService.saveOrUpdateCredentialsForPartenaire(siretPartenaire, body);

		return ResponseEntity.ok(""+credential.getId());
	}

	@Override
	public ResponseEntity<Void> updateCredentials(String siretPartenaire, @Valid Credentials body) {
		partenaireService.saveOrUpdateCredentialsForPartenaire(siretPartenaire, body);

		return ResponseEntity.ok().build();
	}
}
