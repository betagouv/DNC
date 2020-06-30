import antsImg from 'assets/images/ants_logo.png';
import cafImg from 'assets/images/caf_logo.png';
import dataGouvImg from 'assets/images/data_gouv_logo.png';
import dgfipImg from 'assets/images/dgfip_logo_square.png';
import fcImg from 'assets/images/fc_logo_2.png';

const getJustificatifsPersonnalises = props => [
  {
    id: 'autorisation_stationnement',
    label: 'Autorisation de stationnement (déménagement)',
    enabled: true,
    data: [
      {
        img: fcImg,
        label: 'nom',
        value: props.identitePivot?.nomFamille || 'DUBOIS',
      },
      {
        img: fcImg,
        label: 'prénom',
        value: props.identitePivot?.prenoms || 'Angela Claire Louise',
      },
      {
        id: 'adresse',
        label: 'Adresse',
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
    ],
    additionalData: [
      {
        id: 'telephone',
        label: 'Téléphone',
        options: [
          {
            id: 'telephone_1',
            value: '06 12 34 56 78',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'telephone_2',
            value: '07 98 76 54 32',
            source: 'CNAF',
            sourceImg: cafImg,
          },
          {
            id: 'telephone_3',
            value: '06 12 33 21 60',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
        ],
      },
      {
        label: 'Adresse mail',
        source: null,
        img: null,
        values: [
          {
            id: 'adresse_mail',
            label: 'Adresse mail',
            value: 'angela.dubois@gmail.com',
          },
        ],
      },
    ],
  },
  {
    id: 'carte_stationnement',
    label: 'Carte de stationnement résident',
    enabled: true,
    data: [
      {
        img: fcImg,
        label: 'nom',
        value: props.identitePivot?.nomFamille || 'DUBOIS',
      },
      {
        img: fcImg,
        label: 'prénom',
        value: props.identitePivot?.prenoms || 'Angela Claire Louise',
      },
      {
        id: 'adresse',
        label: 'Adresse',
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
      {
        id: 'immatriculation',
        img: antsImg,
        source: 'ANTS',
        label: 'Immatriculation',
        value: '234-XY-56',
        options: [
          { id: 'immatriculation_1', value: '234-XY-56' },
          { id: 'immatriculation_2', value: '789-AB-12' },
        ],
      },
      {
        img: antsImg,
        label: "catégorie crit'air",
        value: '0',
      },
    ],
    additionalData: [
      {
        label: 'Revenus',
        source: 'DGFiP',
        img: dgfipImg,
        values: [
          {
            id: 'revenus',
            label: 'Revenus',
            value: '35 604 €',
          },
        ],
      },
      {
        id: 'telephone',
        label: 'Téléphone',
        options: [
          {
            id: 'telephone_1',
            value: '06 12 34 56 78',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'telephone_2',
            value: '07 98 76 54 32',
            source: 'CNAF',
            sourceImg: cafImg,
          },
          {
            id: 'telephone_3',
            value: '06 12 33 21 60',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
        ],
      },
      /* {
                id: 'immatriculation',
                label: 'Immatriculation',
                source: 'ANTS',
                img: antsImg,
                options: [
                  { id: 'immatriculation_1', value: '234-XY-56' },
                  { id: 'immatriculation_2', value: '789-AB-12' },
                ],
              }, */
      {
        label: 'Validité du permis de conduire',
        source: 'ANTS',
        img: antsImg,
        values: [
          {
            id: 'validite_permis_conduire',
            label: 'Validité du permis de conduire ',
            value: 'Non',
          },
        ],
      },
      {
        label: 'Information de ma société',
        source: 'data.gouv',
        img: dataGouvImg,
        values: [
          {
            id: 'raison_sociale',
            label: 'Raison sociale',
            value: 'SARL Dupont & fils',
          },
          {
            id: 'siret',
            label: 'SIRET',
            value: '802 954 785 00028',
          },
        ],
      },
    ],
  },
  {
    id: 'transport_scolaire',
    label: 'Demande de transport scolaire',
    enabled: true,

    data: [
      {
        img: cafImg,
        label: 'Enfant',
        value: 'DUBOIS Martin',
        source: 'CAF',
        options: [
          {
            id: 'enfant_1',
            value: 'DUBOIS Martin',
          },
          {
            id: 'enfant_2',
            value: 'DUBOIS Jean',
          },
          {
            id: 'enfant_3',
            value: 'DUBOIS Pierre',
          },
        ],
      },
      {
        img: cafImg,
        label: 'quotient_familial',
        value: '400',
        source: 'CAF',
      },
      {
        id: 'adresse',
        label: "Adresse de l'enfant",
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
    ],
    additionalData: [
      {
        label: "Nom de l'établissement",
        source: null,
        img: null,
        values: [
          {
            id: 'etablissement',
            label: "Nom de l'établissement",
            value: 'Ecole Bellebranche',
          },
        ],
      },
      {
        label: 'Adresse mail',
        source: null,
        img: null,
        values: [
          {
            id: 'adresse_mail',
            label: 'Adresse mail',
            value: 'angela.dubois@gmail.com',
          },
        ],
      },
    ],
  },
  {
    id: 'declaration_travaux',
    label: 'Déclaration préalable de travaux',
    data: [],
  },
  {
    id: 'dossier_mariage',
    label: 'Dossier de mariage',
    enabled: true,
    data: [
      {
        img: fcImg,
        label: 'nom',
        value: props.identitePivot?.nomFamille || 'DUBOIS',
      },
      {
        img: fcImg,
        label: 'prénom',
        value: props.identitePivot?.prenoms || 'Angela Claire Louise',
      },
      {
        id: 'adresse',
        label: 'Adresse',
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
      {
        img: null,
        id: 'nationalite',
        label: 'Nationalité',
        value: 'Française',
      },
      {
        img: null,
        id: 'a_joindre',
        label: 'Documents à joindre',
        value: 'Acte de naissance',
      },
    ],
    additionalData: [],
  },
  {
    id: 'inscription_creche',
    label: 'Inscription en crèche',
    enabled: true,

    data: [
      {
        img: fcImg,
        label: 'nom',
        value: props.identitePivot?.nomFamille || 'DUBOIS',
      },
      {
        img: fcImg,
        label: 'prénom',
        value: props.identitePivot?.prenoms || 'Angela Claire Louise',
      },
      {
        img: dgfipImg,
        label: "Déclaration de l'année en cours",
        value: '32 125 €',
        source: 'DGFiP',
      },
      {
        img: cafImg,
        label: "Numéro d'allocataire",
        value: '11 34 56 78 9',
        source: 'CAF',
      },
      {
        id: 'adresse',
        label: 'Adresse',
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
      {
        img: null,
        id: 'a_joindre',
        label: 'Documents à joindre',
        value:
          "Certificat de grossesse, carnet de santé de l'enfant et copie des 3 derniers salaires",
      },
    ],
    additionalData: [
      {
        label: "Nom de l'établissement",
        source: null,
        img: null,
        values: [
          {
            id: 'etablissement',
            label: "Nom de l'établissement",
            value: 'Ecole Bellebranche',
          },
        ],
      },
      {
        label: 'Adresse mail',
        source: null,
        img: null,
        values: [
          {
            id: 'adresse_mail',
            label: 'Adresse mail',
            value: 'angela.dubois@gmail.com',
          },
        ],
      },
    ],
  },
  {
    id: 'inscription_ecole',
    label: 'Inscription en école (maternelle à lycée)',
    enabled: true,
    data: [
      {
        img: cafImg,
        id: 'enfant',
        label: 'Enfant',
        value: 'DUBOIS Martin',
        source: 'CAF',
        options: [
          {
            id: 'enfant_1',
            value: 'DUBOIS Martin',
          },
          {
            id: 'enfant_2',
            value: 'DUBOIS Jean',
          },
          {
            id: 'enfant_3',
            value: 'DUBOIS Pierre',
          },
        ],
      },
      {
        id: 'adresse_enfant',
        label: "Adresse de l'enfant",
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_enfant_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_enfant_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_enfant_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
      {
        img: fcImg,
        label: 'nom du parent',
        value: props.identitePivot?.nomFamille || 'DUBOIS',
      },
      {
        img: fcImg,
        label: 'prénom du parent',
        value: props.identitePivot?.prenoms || 'Angela Claire Louise',
      },
      {
        id: 'adresse_parent',
        label: 'Adresse du parent',
        value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
        img: fcImg,
        options: [
          {
            id: 'adresse_parent_1',
            value: 'BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND',
            source: 'FranceConnect',
            sourceImg: fcImg,
          },
          {
            id: 'adresse_parent_2',
            value: '3 Rue Jean Jaurès 25660 Montrond-le-Château',
            source: 'DGFiP',
            sourceImg: dgfipImg,
          },
          {
            id: 'adresse_parent_3',
            value: '182 Avenue de France - Paris',
            source: 'CNAF',
            sourceImg: cafImg,
          },
        ],
      },
      {
        img: null,
        id: 'a_joindre',
        label: 'Documents à joindre',
        value: 'Historique des vaccins',
      },
    ],
    additionalData: [],
  },
];

export default getJustificatifsPersonnalises;
