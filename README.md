# FoAI23-24
Repository for the project related to the Fundamentals of Artificial Intelligence course, academic year 2023-2024

## Abstract
This project presents the development of a decision support system tailored for software management within a software house. The system leverages ontologies to structure and represent knowledge, utilizing GraphBRAIN for ontology translation and Neo4j for data storage. A custom software ontology was created, integrating with existing ontologies and populated using data extracted from Wikidata via SPARQL queries. The knowledge base was further translated into Prolog, enabling the creation of rules to facilitate decision-making. This documentation outlines the methodology, tools, and processes used, and highlights the system's potential applications in real-world software management scenarios.

## Files of interest for evaluation
1. Documentation: `Pinto_documentazione_foai.pdf`
2. Scheme of the knowledge base in the GBS formalism: `inputs/software_Pinto.gbs`
3. Script that handles sparql query and conversion of results to in the format required by Neo4j: `src/graphs/pinto_script_kb.ipynb`
4. File containing the results of the query: `src/graphs/software_kb.json`
5. File containing the results of the query in Neo4j compatible format: `src/graphs/kb_software_pinto.json`
6. Prolog version of the KB: `inputs/exportedGraph.pl`
7. Prolog version of the KB with instances in list form: `outputs/list_exportedGraph.pl`
8. Prolog version of the KB scheme: `outputs/schema_software_Pinto.pl`
9. Prolog rules: `outputs/prolog_rules.pl`
