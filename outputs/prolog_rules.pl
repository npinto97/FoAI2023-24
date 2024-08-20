%:- use_module(library(theme/light)).
:-style_check(-discontiguous).
:- consult('C:\\Users\\Nicolas\\Desktop\\GraphBRAINAPI\\outputs\\schema_software_Pinto.pl').
:- consult('C:\\Users\\Nicolas\\Desktop\\GraphBRAINAPI\\outputs\\list_exportedGraph.pl').
 
%------------------------------------------------------%
%                        REGOLE                        %
%------------------------------------------------------%


% Nella forma software_of_type('SoftwareName', 'Type'): Verifica se 'SoftwareName' è un software di tipo 'Type' 
% Nella forma software_of_type('SoftwareName', Y): Restituisce di che tipo è 'SoftwareName'
% Nella forma software_of_type(X, 'Type'): Restituisce tutti i software di tipo 'Type'
% Nella forma software_of_type(X, Y): Restituisce tutte le coppie 'SoftwareName'-'Type'
software_of_type(SoftwareName, Type) :-
    node(N, 'Software'),
    node_properties(N, Properties),
    member('name'-SoftwareName, Properties),
    member('softwareType'-Type, Properties).


% Versione limitata al primo risultato del predicato precedente
software_of_single_type(SoftwareName, Type) :-
    software_of_type(SoftwareName, Type),
    !.


% Nella forma software_with_license('SoftwareName', 'License'): Verifica se 'SoftwareName' è un software con licenza 'License'
% Nella forma software_with_license('SoftwareName', Y): Restituisce la licenza di 'SoftwareName' 
% Nella forma software_with_license(X, 'License'): Restituisce tutti i software con licenza 'License'
% Nella forma software_with_license(X, Y): Restituisce tutte le coppie 'SoftwareName'-'License'
software_with_license(SoftwareName, License) :-
    node(N, 'Software'),
    node_properties(N, Properties),
    member('name'-SoftwareName, Properties),
    member('license'-License, Properties).


% Nella forma software_of_type_with_license('SoftwareName', 'Type', 'License'): verifica che 'SoftwareName' sia di tipo 'Type' e abbia licenza 'License'
% Effettua un controllo incrociato a seconda della posizione della/e viariabile/i inserite in fase di interrogazione
software_of_type_with_license(SoftwareName, Type, License) :-
    software_of_type(SoftwareName, Type),
    software_with_license(SoftwareName, License).


% Verifica se 'ReleaseDate' è precedente al 2000.
release_year_before_2000(ReleaseDate) :-
    split_string(ReleaseDate, "-", "", [YearString|_]),
    atom_number(YearString, Year),
    Year < 2000.


% Nella forma software_related_to_stakeholder(SoftwareName, StakeholderName) verifica che SoftwareName sia sviluppato da StakeholderName
% Nella forma software_related_to_stakeholder(SoftwareName, Y) restituisce lo stakeholder che ha sviluppato SoftwareName
% Nella forma software_related_to_stakeholder(X, StakeholderName) restituisce il/i software sviluppato/i da StakeholderName
software_related_to_stakeholder(SoftwareName, StakeholderName) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),

    % Usa l'arco per trovare lo stakeholder che ha sviluppato il software
    arc(_, 'developedBy', SoftwareID, StakeholderID),

    node(StakeholderID, 'Stakeholder'),
    node_properties(StakeholderID, StakeholderProperties),
    member('name'-StakeholderName, StakeholderProperties).


% Versione limitata al primo risultato del predicato precedente
software_related_to_single_stakeholder(SoftwareName, StakeholderName) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),
    !,  

    arc(_, 'developedBy', SoftwareID, StakeholderID),
    !,

    node(StakeholderID, 'Stakeholder'),
    node_properties(StakeholderID, StakeholderProperties),
    member('name'-StakeholderName, StakeholderProperties),
    !.


% Nella forma obsolete_adobe_software(SoftwareName): Verifica che 'SoftwareName' abbia una data di rilascio precedente al 2000.
% Nella forma obsolete_adobe_software(X): Trova software sviluppati da Adobe con data di rilascio precedente al 2000.
obsolete_adobe_software(SoftwareName) :-
    setof(SoftwareName, (
        software_related_to_stakeholder(SoftwareName, 'Adobe'),
        node(SoftwareID, 'Software'),
        node_properties(SoftwareID, SoftwareProperties),
        member('name'-SoftwareName, SoftwareProperties),
        member('presentationDate'-ReleaseDate, SoftwareProperties),
        release_year_before_2000(ReleaseDate)
    ), SoftwareList),
    member(SoftwareName, SoftwareList).


% Nella forma non_obsolete_software('SoftwareName'): Verifica che 'SoftwareName' non sia obsoleto
% Nella forma non_obsolete_software(X): Trova software rilasciati dopo il 2000 (non obsoleti)
% I software senza data sono considerati non obsoleti
non_obsolete_software(SoftwareName) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),

    member('presentationDate'-ReleaseDate, SoftwareProperties),
    \+ release_year_before_2000(ReleaseDate).  % Verifica che l'anno non sia prima del 2000


% Nella forma software_executable_on(SoftwareName, OSName): verifica che SoftwareName sia eseguibile su sistema operativo OSName
% Nella forma software_executable_on(X, OSName): restituisce tutti software eseguibili su sistema operativo OSName
% Nella forma software_executable_on(SoftwareName, Y): restituisce tutti i sistemi operativi su cui è eseguibile SoftwareName
software_executable_on(SoftwareName, OSName) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),

    arc(_, 'executableOn', SoftwareID, OSID),

    node(OSID, 'OperatingSystem'),
    node_properties(OSID, OSProperties),

    member('name'-OSName, OSProperties).


% Versione limitata al primo risultato del predicato precedente
software_executable_on_single(SoftwareName, OSName) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),
    !, 

    arc(_, 'executableOn', SoftwareID, OSID),
    !,

    node(OSID, 'OperatingSystem'),
    node_properties(OSID, OSProperties),
    member('name'-OSName, OSProperties),
    !.  


% Seleziona un software per un progetto basato su criteri multipli.
% La flag ExeOnMacOS attiva o disattiva il controllo che SoftwareName sia eseguibile su MacOS
select_software_for_project(SoftwareName, Type, License, ExeOnMacOS) :-
    software_of_type_with_license(SoftwareName, Type, License),
    (ExeOnMacOS ->
        software_executable_on(SoftwareName, OSName),
        downcase_atom(OSName, OSNameLower),
        sub_string(OSNameLower, _, _, _, 'mac');
        true).


% Regola che verifica se Software1 è un'edizione di Software2 e sono scritti nello stesso linguaggio di programmazione
software_edition_of_same_language(SoftwareName1, SoftwareName2) :-
    node(SoftwareID1, 'Software'),
    node_properties(SoftwareID1, SoftwareProperties1),
    member('name'-SoftwareName1, SoftwareProperties1),

    node(SoftwareID2, 'Software'),
    node_properties(SoftwareID2, SoftwareProperties2),
    member('name'-SoftwareName2, SoftwareProperties2),

    arc(_, 'hasEdition', SoftwareID1, SoftwareID2),

    setof(LanguageID,
          (arc(_, 'writtenIn', SoftwareID1, LanguageID),
           arc(_, 'writtenIn', SoftwareID2, LanguageID)),
          Languages),
    
    Languages \= [].


% Regola per verificare la compatibilità tra due software non obsoleti per un bundle.
compatible_software_bundle(SoftwareName1, SoftwareName2, OSName) :-
    % Verifica che entrambi i software siano non obsoleti
    non_obsolete_software(SoftwareName1),
    non_obsolete_software(SoftwareName2),

    % Stesso stakeholder
    software_related_to_single_stakeholder(SoftwareName1, StakeholderName),
    software_related_to_single_stakeholder(SoftwareName2, StakeholderName),

    % Stesso sistema operativo
    software_executable_on_single(SoftwareName1, OSName),
    software_executable_on_single(SoftwareName2, OSName),

    % Tipi diversi di software
    node(SoftwareID1, 'Software'),
    node_properties(SoftwareID1, SoftwareProperties1),
    member('name'-SoftwareName1, SoftwareProperties1),
    member('softwareType'-Type1, SoftwareProperties1),

    node(SoftwareID2, 'Software'),
    node_properties(SoftwareID2, SoftwareProperties2),
    member('name'-SoftwareName2, SoftwareProperties2),
    member('softwareType'-Type2, SoftwareProperties2),
    
    Type1 \= Type2.

%---------------------------------------------------------

% Verifica che tutti i software nella lista siano non obsoleti
all_non_obsolete([]).
all_non_obsolete([SoftwareName|Rest]) :-
    non_obsolete_software(SoftwareName),
    all_non_obsolete(Rest).


% Verifica che tutti i software nella lista siano sviluppati dallo stesso stakeholder
same_stakeholder([_]).  % Una lista con un solo software è automaticamente valida
same_stakeholder([SoftwareName1, SoftwareName2 | Rest]) :-
    software_related_to_stakeholder(SoftwareName1, StakeholderName),
    software_related_to_stakeholder(SoftwareName2, StakeholderName),
    same_stakeholder([SoftwareName2 | Rest]).


% Verifica che tutti i software nella lista siano eseguibili sullo stesso sistema operativo
all_executable_on([], _).
all_executable_on([SoftwareName|Rest], OSName) :-
    software_executable_on(SoftwareName, OSName),
    all_executable_on(Rest, OSName).


% Estrae i tipi di software da una lista di nomi di software
extract_software_types([], []).
extract_software_types([SoftwareName | Rest], [Type | TypesRest]) :-
    node(SoftwareID, 'Software'),
    node_properties(SoftwareID, SoftwareProperties),
    member('name'-SoftwareName, SoftwareProperties),
    member('softwareType'-Type, SoftwareProperties),
    extract_software_types(Rest, TypesRest).


% Verifica che i software nella lista abbiano tipi diversi
different_software_types(SoftwareList) :-
    extract_software_types(SoftwareList, Types),
    all_different(Types).


% Verifica che tutti i nomi dei software nella lista siano diversi
all_different_names(SoftwareList) :-
    extract_software_names(SoftwareList, Names),
    all_different(Names).


% Verifica che tutti gli elementi in una lista siano diversi
all_different([]).
all_different([H | T]) :-
    \+ member(H, T),
    all_different(T).


% Versione estesa del predicato precedente per mezzo dell'uso di una lista di software
compatible_software_list(SoftwareList, OSName) :-
    all_non_obsolete(SoftwareList),
    same_stakeholder(SoftwareList),
    all_executable_on(SoftwareList, OSName),
    different_software_types(SoftwareList),
    %!,
    all_different(SoftwareList).

%---------------------------------------------------------

% Trova tutti i software prodotti dallo stakeholder specificato
software_by_stakeholder(StakeholderName, SoftwareList) :-
    findall(SoftwareName,
            ( node(SoftwareID, 'Software'),
              node_properties(SoftwareID, SoftwareProperties),
              member('name'-SoftwareName, SoftwareProperties),

              arc(_, 'developedBy', SoftwareID, StakeholderID),
              node(StakeholderID, 'Stakeholder'),
              node_properties(StakeholderID, StakeholderProperties),
              member('name'-StakeholderName, StakeholderProperties)
            ),
            SoftwareList).


% Trova i linguaggi di programmazione dei software specificati
languages_of_software(SoftwareList, LanguageList) :-
    findall(LanguageName,
            ( member(SoftwareName, SoftwareList),
              node(SoftwareID, 'Software'),
              node_properties(SoftwareID, SoftwareProperties),
              member('name'-SoftwareName, SoftwareProperties),

              arc(_, 'writtenIn', SoftwareID, LanguageID),
              node(LanguageID, 'ProgrammingLanguage'),
              node_properties(LanguageID, LanguageProperties),
              member('name'-LanguageName, LanguageProperties)
            ),
            LanguageList).


% Conta i software per ogni linguaggio
count_software_by_language(LanguageList, LanguageCountList) :-
    count_languages(LanguageList, [], LanguageCountList).

% Conta le occorrenze di ciascun linguaggio
count_languages([], Acc, Acc).
count_languages([Language | Rest], Acc, LanguageCountList) :-
    % Aggiorna il contatore per il linguaggio corrente
    update_count(Language, Acc, UpdatedAcc),
    % Ricorsione per processare il resto della lista
    count_languages(Rest, UpdatedAcc, LanguageCountList).

% Aggiorna il conteggio per un linguaggio
update_count(Language, [], [Language-1]).

update_count(Language, [Language-Count | Rest], [Language-NewCount | Rest]) :-
    NewCount is Count + 1.

update_count(Language, [OtherLanguage-Count | Rest], [OtherLanguage-Count | NewRest]) :-
    Language \= OtherLanguage,
    update_count(Language, Rest, NewRest).


% Ottiene linguaggi di programmazione con il corrispondente numero di software prodotti
software_count_by_language_for_stakeholder(StakeholderName, LanguageCountList) :-
    software_by_stakeholder(StakeholderName, SoftwareList),
    languages_of_software(SoftwareList, LanguageList),
    count_software_by_language(LanguageList, LanguageCountList).

%---------------------------------------------------------
