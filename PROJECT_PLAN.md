# Healthcare Appointment Management System - Plan de projet

## Contexte

Concevoir un système de gestion de rendez-vous médicaux entre patients, médecins et cliniques. L'accent est mis sur la **qualité du design logiciel**, la modularité et l'extensibilité - pas sur une implémentation complète.

---

## Acteurs du système

| Acteur | Rôle |
|---|---|
| **Patient** | Parcourir les services, prendre/modifier/annuler des RDV, consulter l'historique |
| **Médecin** | Gérer ses disponibilités, consulter ses RDV (vue journalière/hebdomadaire) |
| **Administrateur** | Gérer les comptes, les rôles, les cliniques, les services, la configuration |

---

## Fonctionnalités à couvrir

### 1. Gestion des utilisateurs
- Inscription sécurisée et authentification
- Gestion du profil personnel
- Accès basé sur les rôles (RBAC)

### 2. Catalogue cliniques & services
- Association service ↔ clinique
- Attributs : nom, description, spécialité, durée, tarif, médecins assignés
- Filtrage par spécialité, localisation, disponibilité

### 3. Prise de rendez-vous
- Demande, modification, annulation de RDV
- Vérification des disponibilités du médecin avant confirmation
- Prévention des conflits de planning
- Cycle de vie du RDV : `SCHEDULED -> CONFIRMED -> COMPLETED | CANCELLED`

### 4. Gestion des disponibilités médecins
- Définir et mettre à jour les plages horaires
- Bloquer des périodes d'indisponibilité
- Cohérence automatique avec les RDV existants

### 5. Facturation & paiement (simulation)
- Sélection du mode de paiement : carte, assurance, portefeuille numérique
- Simulation de confirmation de paiement (pas de vrai traitement)
- Ajustements de prix : réductions d'assurance, promotions, rabais fixes/pourcentage
- Calcul et affichage du coût final avant confirmation

### 6. Notifications
- Événements déclencheurs : confirmation, annulation, rappel, modification de planning
- Canaux : in-app, e-mail, SMS (selon préférences utilisateur)

---

## Livrables attendus

### 1. Rapport écrit (10 pages max, PDF/Word)
- [ ] Énoncé du problème et exigences fonctionnelles
- [ ] **Diagramme de cas d'utilisation** (Use Case)
- [ ] **Diagramme de classes** (UML)
- [ ] **Diagrammes de séquence** (au moins 3 interactions principales)
- [ ] Explication de l'application des principes **SOLID**
- [ ] Explication de l'application des principes **GRASP**
- [ ] Description des **patterns de conception** utilisés (minimum 3)
- [ ] Difficultés rencontrées et réflexions d'équipe

### 2. Présentation (5 slides max)
- [ ] Résumé des choix de conception
- [ ] Présentation des diagrammes
- [ ] Justification des patterns et principes
- [ ] Q&A

### 3. Prototype Java
- [ ] Classes clés : `Appointment`, `Doctor`, `Patient`, `Payment`, `Notification`
- [ ] Démonstration des interactions principales (pas d'implémentation complète)

---

## Principes & Patterns à appliquer

### Principes SOLID
| Principe | Application attendue |
|---|---|
| **S** - Single Responsibility | Chaque classe a une seule responsabilité |
| **O** - Open/Closed | Ouvert à l'extension, fermé à la modification |
| **L** - Liskov Substitution | Sous-types substituables sans casser le comportement |
| **I** - Interface Segregation | Interfaces spécifiques plutôt que générales |
| **D** - Dependency Inversion | Dépendre des abstractions, pas des implémentations |

### Principes GRASP
- Creator, Information Expert, Controller, Low Coupling, High Cohesion, Polymorphism

### Design Patterns (minimum 3 obligatoires)
| Pattern | Usage suggéré |
|---|---|
| **Strategy** | Modes de paiement, stratégies de notification, calcul de prix |
| **Factory** | Création d'objets (RDV, notifications, utilisateurs) |
| **Observer** | Notifications lors de changements d'état des RDV |
| **Singleton** | Gestionnaire de configuration système |
| **State** | Cycle de vie du rendez-vous |

---

## Diagrammes de séquence à produire (suggestions)

1. **Prise de rendez-vous** : Patient -> Système -> Vérification dispo -> Confirmation -> Notification
2. **Annulation d'un RDV** : Patient/Médecin -> Changement d'état -> Notification
3. **Processus de paiement** : Sélection méthode -> Calcul prix final -> Simulation confirmation

---

## Critères d'évaluation

| Critère | Poids |
|---|---|
| Évaluation de groupe (livrables communs) | 50% |
| Évaluation individuelle (écrit + oral technique) | 50% |

**Points clés évalués** : compréhension du design, contribution individuelle, capacité à justifier les décisions architecturales.

---

## Dates importantes

| Événement | Date |
|---|---|
| Soumission du projet | **24 mai 2026** |
| Évaluation technique | **26 mai 2026** |
| Évaluation finale | **2 juin 2026** |

---

## Équipe

Taille : **5 étudiants**
