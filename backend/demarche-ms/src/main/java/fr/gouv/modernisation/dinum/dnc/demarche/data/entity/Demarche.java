package fr.gouv.modernisation.dinum.dnc.demarche.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.time.LocalDateTime;

/**
 * Entity stockant les données de la démarche.
 *
 * @author Sopra Steria Group
 */
@Entity
@Table(name = "demarche",
    uniqueConstraints =
        @UniqueConstraint(name = "demarche_demarche_id_uq",columnNames = {"id_demarche", "id_usager", "siret_partenaire"})
)
@SequenceGenerator(name = "demarche_id_seq", sequenceName = "demarche_id_seq", allocationSize = 50)
public class Demarche {

    /**
     * Identifiant de la démarche.
     * <p>
     * {@link Long}
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "demarche_id_seq")
    private Long id;

    /**
     * version {@link Integer}
     */
    @Version
    private Integer version;

    /**
     * Libellé de la démarche fournit par le partenaire.
     * <p>
     * {@link String}
     */
    @Column(name = "libelle")
    private String libelle;

    /**
     * Statut de la démarche fournit par le partenaire.
     * <p>
     * {@link String}
     */
    @Column(name = "statut")
    private String statut;

    /**
     * Commentaire du partenaire sur la mise à jour du Statut de la démarche fournit par le partenaire.
     * <p>
     * {@link String}
     */
    @Column(name = "commentaires")
    private String commentaires;

    /**
     * Code de la démarche.
     * <p>
     * {@link String}
     */
    @Column(name = "code_demarche")
    private String codeDemarche;

    /**
     * Identifiant du partenaire présent dans le micro-service partenaire.
     * <p>
     * {@link String}
     */
    @Column(name = "siret_partenaire", length = 14)
    private String siretPartenaire;

    /**
     * Identifiant de l'usager associée à cette démarche.
     * <p>
     * {@link String}
     */
    @Column(name = "id_usager")
    private String idUsager;

    /**
     * Identifiant fonctionnel de la démarche associée à cette démarche.
     * <p>
     * {@link String}
     */
    @Column(name = "id_demarche")
    private String idDemarche;

    /**
     * Date de début de la démarche
     */
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    /**
     * Date de fin de la démarche
     */
    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    /**
     * Date de mise à jour de la démarche
     */
    @Column(name = "date_maj")
    private LocalDateTime dateMiseAJour;

    /**
     * Getter du champ id.
     * @return id {@link Long}
     */
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    /**
     * Setter du champ id.
     * @param id {@link Long}
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter du champ version
     * return {@link Integer} la valeur du champ version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Setter du champ version
     *
     * @param version valeur à setter
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Getter du champ libelle.
     * @return libelle {@link String}
     */
    @JsonProperty("libelle")
    public String getLibelle() {
        return libelle;
    }

    /**
     * Setter du champ libelle.
     * @param libelle {@link String}
     */
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    /**
     * Getter du champ statut.
     * @return statut {@link String}
     */
    @JsonProperty("statut")
    public String getStatut() {
        return statut;
    }

    /**
     * Setter du champ statut.
     * @param statut {@link String}
     */
    public void setStatut(String statut) {
        this.statut = statut;
    }

    /**
     * Getter du champ commentaires
     *
     * @return {@link String} la valeur du champ commentaires
     */
    @JsonProperty("commentaires")
    public String getCommentaires() {
        return commentaires;
    }

    /**
     * Setter du champ commentaires
     *
     * @param commentaires valeur à setter
     */
    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    /**
     * Getter du champ codeDemarche
     *
     * @return {@link String} la valeur du champ codeDemarche
     */
    @JsonProperty("codeDemarche")
    public String getCodeDemarche() {
        return codeDemarche;
    }

    /**
     * Setter du champ codeDemarche
     *
     * @param codeDemarche valeur à setter
     */
    public void setCodeDemarche(String codeDemarche) {
        this.codeDemarche = codeDemarche;
    }

    /**
     * Getter du champ idPartenaire.
     * @return idPartenaire {@link String}
     */
    @JsonProperty("idPartenaire")
    public String getSiretPartenaire() {
        return siretPartenaire;
    }

    /**
     * Setter du champ idPartenaire.
     * @param idPartenaire {@link String}
     */
    public void setSiretPartenaire(String idPartenaire) {
        this.siretPartenaire = idPartenaire;
    }

    /**
     * Getter du champ idUsager
     * return {@link String} la valeur du champ idUsager
     */
    public String getIdUsager() {
        return idUsager;
    }

    /**
     * Setter du champ idUsager
     *
     * @param idUsager valeur à setter
     */
    public void setIdUsager(String idUsager) {
        this.idUsager = idUsager;
    }

    /**
     * Getter du champ idDemarche
     * return {@link String} la valeur du champ idDemarche
     */
    public String getIdDemarche() {
        return idDemarche;
    }

    /**
     * Setter du champ idDemarche
     *
     * @param idDemarche valeur à setter
     */
    public void setIdDemarche(String idDemarche) {
        this.idDemarche = idDemarche;
    }

    /**
     * Getter du champ dateDebut
     * return {@link LocalDateTime} la valeur du champ dateDebut
     */
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    /**
     * Setter du champ dateDebut
     *
     * @param dateDebut valeur à setter
     */
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * Getter du champ dateFin
     * return {@link LocalDateTime} la valeur du champ dateFin
     */
    public LocalDateTime getDateFin() {
        return dateFin;
    }

    /**
     * Setter du champ dateFin
     *
     * @param dateFin valeur à setter
     */
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * Getter du champ dateMiseAJour
     * return {@link LocalDateTime} la valeur du champ dateMiseAJour
     */
    public LocalDateTime getDateMiseAJour() {
        return dateMiseAJour;
    }

    /**
     * Setter du champ dateMiseAJour
     *
     * @param dateMiseAJour valeur à setter
     */
    public void setDateMiseAJour(LocalDateTime dateMiseAJour) {
        this.dateMiseAJour = dateMiseAJour;
    }
}
