package fr.gouv.modernisation.dinum.dnc.common.utils;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe utilitaire pour les opérations sur les usagers.
 */
public class UsagerUtils {

	/**
	 * Constructeur privée
	 */
	private UsagerUtils() {
		//Constructeur privée pour ne pas avoir de constructeur par défaut
	}
	/**
	 * Construction d'un Identifiant de l'usager à partir de son identité pivot.
	 * Se base sur le nom, le prénom, la date et le lieu de naissance.
	 * Renvoie {@code null} si #identitePivot est {@code null}.
	 * FIXME La fonction n'est pas définitive et devrait être testé en profondeur
	 * @param identitePivot {@link IdentitePivot} identité pivot de l'usager
	 * @return la
	 */
	public static String getUsagerIdFromIdentitePivot(IdentitePivot identitePivot) {
		if(Objects.isNull(identitePivot)) {
			return null;
		}


		return UUID.nameUUIDFromBytes(("DNC:ID_USAGER:" + StringUtils.join(":",
				identitePivot.getFamilyName(),identitePivot.getGivenName(),
				identitePivot.getGender(), identitePivot.getBirthcountry(),
				Objects.isNull(identitePivot.getBirthdate()) ? "" : DateTimeFormatter.ofPattern("yyyy-MM-dd").format(identitePivot.getBirthdate()),
				identitePivot.getBirthcountry()
				)).getBytes(StandardCharsets.UTF_8)).toString();
	}
}
