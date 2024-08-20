:-style_check(-discontiguous).

:- use_module(library(lists)).
:- discontiguous inverse_of/2.
:- discontiguous subclass_of/2.
:- unknown(_, fail).


% ENTITIES

% Entities hierarchy

subclass_of('Organization', 'Agent').
subclass_of('Club', 'Organization').
subclass_of('Company', 'Organization').
subclass_of('EducationalInstitution', 'Organization').
subclass_of('Government', 'Organization').
subclass_of('NonGovernmentalOrganization', 'Organization').
subclass_of('ProfessionalAssociationUnion', 'Organization').
subclass_of('Institution', 'Organization').
subclass_of('PublicInstitution', 'Organization').
subclass_of('SubGroup', 'Organization').
subclass_of('Person', 'Agent').
subclass_of('Speaker', 'Person').
subclass_of('User', 'Agent').
subclass_of('PersonUser', 'User').
subclass_of('OrganizationUser', 'User').
subclass_of('GroupUser', 'User').
subclass_of('Stakeholder', 'Agent').
subclass_of('IndividualStakeholder', 'Stakeholder').
subclass_of('Investor', 'IndividualStakeholder').
subclass_of('Vendor', 'IndividualStakeholder').
subclass_of('ExecutiveSponsor', 'IndividualStakeholder').
subclass_of('RequirementsReviewer', 'IndividualStakeholder').
subclass_of('RequirementsStakeholder', 'IndividualStakeholder').
subclass_of('Customer', 'RequirementsStakeholder').
subclass_of('EndUser', 'RequirementsStakeholder').
subclass_of('RequirementsEngineer', 'IndividualStakeholder').
subclass_of('Programmer', 'IndividualStakeholder').
subclass_of('CodeReviewer', 'IndividualStakeholder').
subclass_of('TeamStakeholder', 'Stakeholder').

subclass_of('Artwork', 'Artifact').
subclass_of('Handicraft', 'Artifact').
subclass_of('IndustrialWork', 'Artifact').
subclass_of('Component', 'IndustrialWork').
subclass_of('AuxiliaryBoard', 'Component').
subclass_of('Capacitor', 'Component').
subclass_of('ExpansionBoard', 'Component').
subclass_of('Chip', 'Component').
subclass_of('Logic', 'Chip').
subclass_of('Buffer', 'Logic').
subclass_of('CoProcessor', 'Logic').
subclass_of('Decoder', 'Logic').
subclass_of('FlipFlop', 'Logic').
subclass_of('Gate', 'Logic').
subclass_of('Memory', 'Logic').
subclass_of('EPROM', 'Memory').
subclass_of('PROM', 'Memory').
subclass_of('RAM', 'Memory').
subclass_of('ROM', 'Memory').
subclass_of('MicroProcessor', 'Chip').
subclass_of('PLA', 'Chip').
subclass_of('RRIOT', 'Chip').
subclass_of('MainBoard', 'Component').
subclass_of('Resistor', 'Component').
subclass_of('Device', 'IndustrialWork').
subclass_of('Calculator', 'Device').
subclass_of('AddingMachine', 'Calculator').
subclass_of('MechanicCalculator', 'Calculator').
subclass_of('ElectricCalculator', 'Calculator').
subclass_of('ElectroMechanicCalculator', 'Calculator').
subclass_of('ElectronicCalculator', 'Calculator').
subclass_of('Computer', 'Device').
subclass_of('Board', 'Computer').
subclass_of('Home', 'Computer').
subclass_of('Mainframe', 'Computer').
subclass_of('Mini', 'Computer').
subclass_of('Personal', 'Computer').
subclass_of('InputDevice', 'Device').
subclass_of('Keyboard', 'InputDevice').
subclass_of('Mouse', 'InputDevice').
subclass_of('Scanner', 'InputDevice').
subclass_of('GraphicPad', 'InputDevice').
subclass_of('Paddle', 'InputDevice').
subclass_of('Joystick', 'InputDevice').
subclass_of('LightPen', 'InputDevice').
subclass_of('WebCam', 'InputDevice').
subclass_of('OutputDevice', 'Device').
subclass_of('Monitor', 'OutputDevice').
subclass_of('Printer', 'OutputDevice').
subclass_of('Plotter', 'OutputDevice').
subclass_of('InputOutputDevice', 'Device').
subclass_of('AcousticCoupler', 'InputOutputDevice').
subclass_of('DockingStation', 'InputOutputDevice').
subclass_of('Modem', 'InputOutputDevice').
subclass_of('TouchScreen', 'InputOutputDevice').
subclass_of('StorageDevice', 'Device').
subclass_of('CardPuncher', 'StorageDevice').
subclass_of('PunchedCardPlayer', 'StorageDevice').
subclass_of('TapePlayer', 'StorageDevice').
subclass_of('CoilTapePlayer', 'StorageDevice').
subclass_of('DiskPackPlayer', 'StorageDevice').
subclass_of('CassetteTapePlayer', 'StorageDevice').
subclass_of('MagneticDiskSinglePlayer', 'StorageDevice').
subclass_of('MagneticDiskDoublePlayer', 'StorageDevice').
subclass_of('HardDisk', 'StorageDevice').
subclass_of('Zip', 'StorageDevice').
subclass_of('StorageMedium', 'Device').
subclass_of('NonComputers', 'Device').
subclass_of('Console', 'NonComputers').
subclass_of('Phone', 'NonComputers').
subclass_of('Typewriter', 'NonComputers').
subclass_of('ElectricTypewriter', 'Typewriter').
subclass_of('MechanicalTypewriter', 'Typewriter').
subclass_of('Watch', 'NonComputers').
subclass_of('Server', 'Device').
subclass_of('Software', 'Artifact').
subclass_of('Development', 'Software').
subclass_of('IDE', 'Development').
subclass_of('Translator', 'Development').
subclass_of('TextEditor', 'Development').
subclass_of('DebuggingTool', 'Development').
subclass_of('VersionControlSystem', 'Development').
subclass_of('Educational', 'Software').
subclass_of('LearningManagementSystem', 'Educational').
subclass_of('Courseware', 'Educational').
subclass_of('SimulationSoftware', 'Educational').
subclass_of('InteractiveWhiteboardSoftware', 'Educational').
subclass_of('EducationalGame', 'Educational').
subclass_of('LanguageLearningSoftware', 'Educational').
subclass_of('Embedded', 'Software').
subclass_of('OfficeAutomation', 'Software').
subclass_of('Browser', 'OfficeAutomation').
subclass_of('Database', 'OfficeAutomation').
subclass_of('EmailClient', 'OfficeAutomation').
subclass_of('Graphics', 'OfficeAutomation').
subclass_of('Presentation', 'OfficeAutomation').
subclass_of('Spreadsheet', 'OfficeAutomation').
subclass_of('TextProcessor', 'OfficeAutomation').
subclass_of('SchedulingTool', 'OfficeAutomation').
subclass_of('OperatingSystem', 'Software').
subclass_of('Videogame', 'Software').
subclass_of('Adventure', 'Videogame').
subclass_of('Maze', 'Videogame').
subclass_of('Sports', 'Videogame').
subclass_of('Simulation', 'Videogame').
subclass_of('Action', 'Videogame').
subclass_of('RolePlayingGame', 'Videogame').
subclass_of('Strategy', 'Videogame').
subclass_of('Fighting', 'Videogame').
subclass_of('Platformer', 'Videogame').
subclass_of('Horror', 'Videogame').
subclass_of('SoftwareComponent', 'Artifact').
subclass_of('SoftwareSystem', 'SoftwareComponent').
subclass_of('Program', 'SoftwareComponent').
subclass_of('Code', 'SoftwareComponent').
subclass_of('SourceCode', 'Code').
subclass_of('MachineCode', 'Code').
subclass_of('InformationItem', 'Artifact').
subclass_of('Documentation', 'InformationItem').
subclass_of('ComponentDescription', 'Documentation').
subclass_of('Pseudocode', 'Documentation').
subclass_of('IntentDocumentation', 'Documentation').
subclass_of('SpecificationDocument', 'IntentDocumentation').
subclass_of('SystemSpecification', 'SpecificationDocument').
subclass_of('ProgramSpecification', 'SpecificationDocument').
subclass_of('BugReport', 'IntentDocumentation').
subclass_of('UserStory', 'IntentDocumentation').
subclass_of('UseCase', 'IntentDocumentation').
subclass_of('TestCase', 'IntentDocumentation').
subclass_of('AgreementEmail', 'IntentDocumentation').
subclass_of('DesignDocument', 'IntentDocumentation').
subclass_of('CodeReviewReport', 'IntentDocumentation').
subclass_of('RequirementsDocument', 'IntentDocumentation').
subclass_of('RequirementsEvaluationDocument', 'IntentDocumentation').
subclass_of('RequirementsAgreement', 'IntentDocumentation').
subclass_of('Intent', 'Documentation').
subclass_of('AdvertisingMaterial', 'InformationItem').
subclass_of('SlideDeck', 'InformationItem').
subclass_of('Slide', 'InformationItem').
subclass_of('RequirementArtifact', 'InformationItem').
subclass_of('Model', 'Artifact').
subclass_of('ConceptualModel', 'Model').

subclass_of('Family', 'Collection').
subclass_of('Group', 'Collection').
subclass_of('Series', 'Collection').
subclass_of('Vocabulary', 'Collection').
subclass_of('Corpus', 'Collection').

subclass_of('Category', 'ContentDescription').
subclass_of('Concept', 'Category').
subclass_of('Genre', 'Category').
subclass_of('Period', 'Category').
subclass_of('Stuff', 'Category').
subclass_of('Subject', 'Category').
subclass_of('Trend', 'Category').
subclass_of('Language', 'ContentDescription').
subclass_of('Text', 'ContentDescription').
subclass_of('Sentence', 'Text').
subclass_of('Word', 'Text').

subclass_of('Audio', 'Document').
subclass_of('Music', 'Audio').
subclass_of('Noise', 'Audio').
subclass_of('Song', 'Audio').
subclass_of('Speech', 'Audio').
subclass_of('Printable', 'Document').
subclass_of('Book', 'Printable').
subclass_of('Booklet', 'Printable').
subclass_of('Card', 'Printable').
subclass_of('Cartography', 'Printable').
subclass_of('DesignDiagram', 'Printable').
subclass_of('Leaflet', 'Printable').
subclass_of('Letter', 'Printable').
subclass_of('Magazine', 'Printable').
subclass_of('Manual', 'Printable').
subclass_of('MusicalScore', 'Printable').
subclass_of('Picture', 'Printable').
subclass_of('Postcard', 'Printable').
subclass_of('Poster', 'Printable').
subclass_of('TextDocument', 'Printable').
subclass_of('HandPrintedBook', 'Printable').
subclass_of('Video', 'Document').
subclass_of('Documentary', 'Video').
subclass_of('Movie', 'Video').
subclass_of('Microform', 'Video').
subclass_of('Projection', 'Video').
subclass_of('Visual', 'Document').

subclass_of('Concert', 'Event').
subclass_of('Conference', 'Event').
subclass_of('Exhibition', 'Event').
subclass_of('Fair', 'Event').
subclass_of('HistoricalEvent', 'Event').
subclass_of('Lecture', 'Event').
subclass_of('Show', 'Event').

subclass_of('Algorithm', 'IntellectualWork').
subclass_of('Approach', 'IntellectualWork').
subclass_of('Discipline', 'IntellectualWork').
subclass_of('Invention', 'IntellectualWork').
subclass_of('ProgrammingLanguage', 'IntellectualWork').
subclass_of('Standard', 'IntellectualWork').
subclass_of('Taxonomy', 'IntellectualWork').
subclass_of('Technology', 'IntellectualWork').
subclass_of('Theorem', 'IntellectualWork').
subclass_of('TheoreticalModel', 'IntellectualWork').
subclass_of('WorkOfArt', 'IntellectualWork').
subclass_of('CartographicWork', 'WorkOfArt').
subclass_of('Expression', 'WorkOfArt').
subclass_of('MusicalWork', 'WorkOfArt').
subclass_of('PointOfInterest', 'IntellectualWork').
subclass_of('PlaceToVisit', 'PointOfInterest').
subclass_of('Archive', 'PlaceToVisit').
subclass_of('Architecture', 'PlaceToVisit').
subclass_of('Church', 'PlaceToVisit').
subclass_of('Library', 'PlaceToVisit').
subclass_of('Monument', 'PlaceToVisit').
subclass_of('Museum', 'PlaceToVisit').
subclass_of('Palace', 'PlaceToVisit').
subclass_of('PanoramicView', 'PlaceToVisit').
subclass_of('Park', 'PlaceToVisit').
subclass_of('Ruin', 'PlaceToVisit').
subclass_of('Theater', 'PlaceToVisit').

subclass_of('Domain', 'InternetComponent').
subclass_of('EMailAddress', 'InternetComponent').
subclass_of('Protocol', 'InternetComponent').
subclass_of('URI', 'InternetComponent').
subclass_of('URL', 'URI').

subclass_of('ComponentItem', 'Item').
subclass_of('DeviceItem', 'Item').
subclass_of('DocumentItem', 'Item').
subclass_of('SoftwareItem', 'Item').
subclass_of('SystemItem', 'Item').

subclass_of('Identifier', 'Nomen').

subclass_of('Administrative', 'Place').
subclass_of('Continent', 'Administrative').
subclass_of('Country', 'Administrative').
subclass_of('Region/State', 'Administrative').
subclass_of('Province/County', 'Administrative').
subclass_of('Town', 'Administrative').
subclass_of('District', 'Administrative').
subclass_of('Road/Street/Square', 'Administrative').
subclass_of('Address', 'Administrative').
subclass_of('Building', 'Place').
subclass_of('Apartment', 'Building').
subclass_of('Castle', 'Building').
subclass_of('Cottage', 'Building').
subclass_of('House', 'Building').
subclass_of('Mansion', 'Building').
subclass_of('Skyscraper', 'Building').
subclass_of('Geographic', 'Place').
subclass_of('Area', 'Geographic').
subclass_of('Hill', 'Geographic').
subclass_of('Island', 'Geographic').
subclass_of('Lake', 'Geographic').
subclass_of('Land', 'Geographic').
subclass_of('Mountain', 'Geographic').
subclass_of('Peninsula', 'Geographic').
subclass_of('Planet', 'Geographic').
subclass_of('River', 'Geographic').
subclass_of('Sea', 'Geographic').
subclass_of('Valley', 'Geographic').
subclass_of('Beach', 'Geographic').
subclass_of('Basin', 'Geographic').
subclass_of('Butte', 'Geographic').
subclass_of('Canyon', 'Geographic').
subclass_of('Cave', 'Geographic').
subclass_of('Plateau', 'Geographic').

subclass_of('Activity', 'ProcessComponent').
subclass_of('Case', 'ProcessComponent').
subclass_of('Process', 'ProcessComponent').
subclass_of('Task', 'ProcessComponent').

subclass_of('Time', 'TemporalSpecification').
subclass_of('TimeInterval', 'TemporalSpecification').
subclass_of('Timeline', 'TemporalSpecification').
subclass_of('TimePoint', 'TemporalSpecification').
subclass_of('Year', 'TimePoint').
subclass_of('Month', 'TimePoint').
subclass_of('Day', 'TimePoint').
subclass_of('DateTime', 'TimePoint').

subclass_of('Education', 'Award').
subclass_of('Prize', 'Award').
subclass_of('Record', 'Award').

subclass_of('FileSystemItem', 'DigitalItem').
subclass_of('Directory', 'FileSystemItem').

subclass_of('PerformedSimpleActivity', 'PerformedActivity').
subclass_of('CodeDevelopment', 'PerformedSimpleActivity').
subclass_of('CodeDocumentation', 'PerformedSimpleActivity').
subclass_of('CodeReview', 'PerformedSimpleActivity').
subclass_of('PerformedCompositeActivity', 'PerformedActivity').
subclass_of('RequirementsElicitation', 'PerformedCompositeActivity').
subclass_of('ConceptualModeling', 'PerformedCompositeActivity').
subclass_of('RequirementsDocumentation', 'PerformedCompositeActivity').
subclass_of('RequirementsVerification', 'PerformedCompositeActivity').
subclass_of('RequirementsNegotiation', 'PerformedCompositeActivity').
subclass_of('Coding', 'PerformedCompositeActivity').

subclass_of('FunctionalRequirement', 'Requirement').
subclass_of('NonFunctionalRequirement', 'Requirement').

% Entities schema

'Agent'([name]).
'Organization'([acronym, form, foundationDate, closingDate, id, email, address, telephoneNumber]).
'Club'([]).
'Company'([]).
'EducationalInstitution'([]).
'Government'([]).
'NonGovernmentalOrganization'([]).
'ProfessionalAssociationUnion'([]).
'Institution'([]).
'PublicInstitution'([]).
'SubGroup'([]).
'Person'([surname, title, knownAs, nickname, gender, bornIn, birthDate, diedIn, deathDate, nationality, job, telephoneNumber, socialSecurityNumber, email, biography, socialMediaProfile]).
'Speaker'([speechTopic, speechLanguage]).
'User'([username, description, registrationDate, eraseDate]).
'PersonUser'([]).
'OrganizationUser'([]).
'GroupUser'([]).
'Stakeholder'([stakeholderId, contactInformation, availability]).
'IndividualStakeholder'([skills, experience, internal]).
'Investor'([investmentType, returnInvestment]).
'Vendor'([]).
'ExecutiveSponsor'([]).
'RequirementsReviewer'([]).
'RequirementsStakeholder'([]).
'Customer'([serviceNeeded]).
'EndUser'([]).
'RequirementsEngineer'([]).
'Programmer'([]).
'CodeReviewer'([]).
'TeamStakeholder'([]).

'Artifact'([name, description, creation, issued, submitted, accepted]).
'Artwork'([]).
'Handicraft'([]).
'IndustrialWork'([]).
'Component'([acronym, technology, originalPrice, announcementDate, presentationDate, startDate, endDate]).
'AuxiliaryBoard'([]).
'Capacitor'([]).
'ExpansionBoard'([]).
'Chip'([]).
'Logic'([]).
'Buffer'([]).
'CoProcessor'([]).
'Decoder'([]).
'FlipFlop'([type]).
'Gate'([function]).
'Memory'([capacity, speed]).
'EPROM'([]).
'PROM'([]).
'RAM'([]).
'ROM'([content]).
'MicroProcessor'([speed, bits]).
'PLA'([]).
'RRIOT'([]).
'MainBoard'([]).
'Resistor'([]).
'Device'([partNumber, originalPrice, announcementDate, presentationDate, startDate, endDate]).
'Calculator'([]).
'AddingMachine'([]).
'MechanicCalculator'([]).
'ElectricCalculator'([]).
'ElectroMechanicCalculator'([]).
'ElectronicCalculator'([]).
'Computer'([]).
'Board'([]).
'Home'([]).
'Mainframe'([]).
'Mini'([]).
'Personal'([]).
'InputDevice'([]).
'Keyboard'([layout, keys, technology]).
'Mouse'([buttons, technology]).
'Scanner'([size, resolution, colors]).
'GraphicPad'([]).
'Paddle'([]).
'Joystick'([]).
'LightPen'([]).
'WebCam'([]).
'OutputDevice'([]).
'Monitor'([size, resolution, frequency, standard, technology]).
'Printer'([technology, size, resolution, speed]).
'Plotter'([]).
'InputOutputDevice'([]).
'AcousticCoupler'([]).
'DockingStation'([]).
'Modem'([]).
'TouchScreen'([]).
'StorageDevice'([]).
'CardPuncher'([]).
'PunchedCardPlayer'([]).
'TapePlayer'([]).
'CoilTapePlayer'([]).
'DiskPackPlayer'([]).
'CassetteTapePlayer'([]).
'MagneticDiskSinglePlayer'([]).
'MagneticDiskDoublePlayer'([]).
'HardDisk'([]).
'Zip'([]).
'StorageMedium'([capacity, technology, appearance]).
'NonComputers'([]).
'Console'([]).
'Phone'([]).
'Typewriter'([]).
'ElectricTypewriter'([]).
'MechanicalTypewriter'([]).
'Watch'([]).
'Server'([hostname, ipAddress, storageCapacity]).
'Software'([acronym, version, announcementDate, presentationDate, startDate, endDate, license, developmentStatus, price, softwareType, softwareCategory]).
'Development'([]).
'IDE'([]).
'Translator'([]).
'TextEditor'([]).
'DebuggingTool'([]).
'VersionControlSystem'([]).
'Educational'([subjectArea, gradeLevel, learningObjective]).
'LearningManagementSystem'([]).
'Courseware'([]).
'SimulationSoftware'([]).
'InteractiveWhiteboardSoftware'([]).
'EducationalGame'([]).
'LanguageLearningSoftware'([]).
'Embedded'([]).
'OfficeAutomation'([]).
'Browser'([]).
'Database'([]).
'EmailClient'([]).
'Graphics'([]).
'Presentation'([]).
'Spreadsheet'([]).
'TextProcessor'([]).
'SchedulingTool'([]).
'OperatingSystem'([architecture]).
'Videogame'([]).
'Adventure'([]).
'Maze'([]).
'Sports'([]).
'Simulation'([]).
'Action'([]).
'RolePlayingGame'([]).
'Strategy'([]).
'Fighting'([]).
'Platformer'([]).
'Horror'([]).
'SoftwareComponent'([acronym, license, version, developmentStatus, endDate]).
'SoftwareSystem'([]).
'Program'([]).
'Code'([]).
'SourceCode'([linesOfCode]).
'MachineCode'([executionEnvironment]).
'InformationItem'([language, body, edition, creationDate]).
'Documentation'([version, license, owner, lastUpdateDate]).
'ComponentDescription'([DevelopmentStatus]).
'Pseudocode'([TimeComplexvityValue, SpaceComplexfityValue]).
'IntentDocumentation'([ID, priority, developmentStatus, approvalStatus]).
'SpecificationDocument'([scope, audience]).
'SystemSpecification'([]).
'ProgramSpecification'([]).
'BugReport'([reportDate, severity, status, reporter]).
'UserStory'([storyPoints, acceptanceCriteria, sprint]).
'UseCase'([actor, trigger, precondition, postcondition, mainFlow, alternateFlow, exceptionFlow, frequency]).
'TestCase'([exceptedResult, actualResult, testData, testEnvironment, testSetup, testPassCriteria]).
'AgreementEmail'([subject, recipient, attachments]).
'DesignDocument'([]).
'CodeReviewReport'([]).
'RequirementsDocument'([]).
'RequirementsEvaluationDocument'([]).
'RequirementsAgreement'([]).
'Intent'([]).
'AdvertisingMaterial'([medium, targetAudience, distributionChannel, approvalStatus]).
'SlideDeck'([targetAudience, duration, version, distributionChannels]).
'Slide'([pageNumber]).
'RequirementArtifact'([]).
'Model'([]).
'ConceptualModel'([]).

'Collection'([name, acronym, entityType, startDate, endDate, accrualPeriodicity, accrualPolicy]).
'Family'([]).
'Group'([]).
'Series'([]).
'Vocabulary'([]).
'Corpus'([]).

'ContentDescription'([name, description]).
'Category'([id, taxonomy, lwnCode]).
'Concept'([]).
'Genre'([]).
'Period'([]).
'Stuff'([]).
'Subject'([]).
'Trend'([]).
'Language'([name, alpha2, alpha3b, nameEnglish, nameFrench, nameGerman]).
'Text'([value]).
'Sentence'([]).
'Word'([lemma, pos, language, multiWordExpression]).

'Document'([language, copyright, format, length, originalPrice, useRights, title, additionalInfoTitle, edition, place, date, captureMode, size, id, sourceForAuthorization, termsOfAvailability, accessRestrictions, generation]).
'Audio'([playingSpeed, grooveWidth, kindOfCutting, tapeConfiguration, kindOfSound, specialReproduction]).
'Music'([]).
'Noise'([]).
'Song'([]).
'Speech'([]).
'Printable'([]).
'Book'([typeface, typeSize]).
'Booklet'([]).
'Card'([]).
'Cartography'([scale]).
'DesignDiagram'([]).
'Leaflet'([]).
'Letter'([]).
'Magazine'([]).
'Manual'([]).
'MusicalScore'([key]).
'Picture'([]).
'Postcard'([]).
'Poster'([]).
'TextDocument'([]).
'HandPrintedBook'([foliation, collation]).
'Video'([]).
'Documentary'([]).
'Movie'([]).
'Microform'([]).
'Projection'([]).
'Visual'([]).

'Event'([name, acronym, description, startDate, endDate]).
'Concert'([]).
'Conference'([]).
'Exhibition'([]).
'Fair'([]).
'HistoricalEvent'([]).
'Lecture'([]).
'Show'([]).

'IntellectualWork'([name, description, role, creation, patent, date]).
'Algorithm'([]).
'Approach'([]).
'Discipline'([]).
'Invention'([]).
'ProgrammingLanguage'([paradigm, standardization]).
'Standard'([]).
'Taxonomy'([acronym, version]).
'Technology'([]).
'Theorem'([]).
'TheoreticalModel'([]).
'WorkOfArt'([name, description, role, creation, patent, title, id, date, additionalFeatures, intendedTermination]).
'CartographicWork'([coordinates, equinox]).
'Expression'([title, language, edition, copyright, length, date, useRights]).
'MusicalWork'([number, key]).
'PointOfInterest'([name, place, description, address, phone, estimatedCost]).
'PlaceToVisit'([timeLimit, requiresTicket]).
'Archive'([]).
'Architecture'([]).
'Church'([]).
'Library'([]).
'Monument'([]).
'Museum'([]).
'Palace'([]).
'PanoramicView'([]).
'Park'([]).
'Ruin'([]).
'Theater'([]).

'InternetComponent'([description]).
'Domain'([superdomain]).
'EMailAddress'([username, superdomain]).
'Protocol'([superdomain]).
'URI'([protocol, superdomain, path]).
'URL'([]).

'Item'([madeIn, useRights, serialNo, description, productionDate, working, complete, boxed]).
'ComponentItem'([instanceOf]).
'DeviceItem'([instanceOf]).
'DocumentItem'([instanceOf]).
'SoftwareItem'([instanceOf]).
'SystemItem'([instanceOf]).

'Nomen'([name, script]).
'Identifier'([taxonomy, vocabulary]).

'Place'([name, language, latitude, longitude, description]).
'Administrative'([codeISO]).
'Continent'([]).
'Country'([codeISOalpha3, codeISOnumeric]).
'Region/State'([]).
'Province/County'([]).
'Town'([]).
'District'([isIn]).
'Road/Street/Square'([isIn]).
'Address'([isIn]).
'Building'([]).
'Apartment'([]).
'Castle'([]).
'Cottage'([]).
'House'([]).
'Mansion'([]).
'Skyscraper'([]).
'Geographic'([]).
'Area'([]).
'Hill'([]).
'Island'([]).
'Lake'([]).
'Land'([]).
'Mountain'([]).
'Peninsula'([]).
'Planet'([]).
'River'([]).
'Sea'([]).
'Valley'([]).
'Beach'([]).
'Basin'([]).
'Butte'([]).
'Canyon'([]).
'Cave'([]).
'Plateau'([]).

'ProcessComponent'([name]).
'Activity'([dateTimeStart, dateTimeEnd, contentRef]).
'Case'([]).
'Process'([]).
'Task'([]).

'TemporalSpecification'([name, description]).
'Time'([hours, minutes, seconds, milliseconds]).
'TimeInterval'([startTime, endTime]).
'Timeline'([]).
'TimePoint'([date]).
'Year'([year]).
'Month'([belongsTo, month]).
'Day'([belongsTo, day]).
'DateTime'([belongsTo, time]).

'Citation'([text, description]).

'Edition'([title, id, edition, copyright, length, creation, issued, submitted, accepted]).

'Award'([name, date, description]).
'Education'([]).
'Prize'([]).
'Record'([]).

'Configuration'([name]).

'Fix'([item, description, startDate, endDate, faultyPart, replacement]).

'Package'([description]).

'System'([type, name]).

'DigitalItem'([]).
'FileSystemItem'([]).
'Directory'([name, path, author, creationDate, lastUpdateDate, lastAccessDate]).

'PerformedActivity'([]).
'PerformedSimpleActivity'([]).
'CodeDevelopment'([]).
'CodeDocumentation'([]).
'CodeReview'([]).
'PerformedCompositeActivity'([]).
'RequirementsElicitation'([]).
'ConceptualModeling'([]).
'RequirementsDocumentation'([]).
'RequirementsVerification'([]).
'RequirementsNegotiation'([]).
'Coding'([]).

'Requirement'([]).
'FunctionalRequirement'([]).
'NonFunctionalRequirement'([]).


% RELATIONSHIPS

% Relationships hierarchy

% Relationships schema

'aliasOf'(['Artifact'-'Artifact', 'Category'-'Category', 'Document'-'Document', 'Organization'-'Organization', 'Person'-'Person', 'Place'-'Place', 'User'-'Person', 'Device'-'Device', 'Person'-'Stakeholder'], [date]).
inverse_of('aliasOf', 'aliasOf').

'attributeOf'(['Category'-'Category'], []).
inverse_of('hasAttribute', 'attributeOf').

'belongsTo'(['Category'-'Category', 'Collection'-'Collection', 'Nomen'-'Collection', 'TemporalSpecification'-'Timeline', 'Word'-'Collection', 'Word'-'Taxonomy', 'Artifact'-'Collection', 'Document'-'Collection', 'Event'-'Collection', 'IntellectualWork'-'Collection', 'Item'-'Collection', 'Person'-'Collection', 'Place'-'Collection', 'PointOfInterest'-'Collection', 'Award'-'Collection', 'Component'-'Collection', 'Device'-'Collection', 'System'-'Collection', 'Stakeholder'-'TeamStakeholder', 'Slide'-'SlideDeck', 'IntentDocumentation'-'Intent', 'RequirementArtifact'-'RequirementsDocument'], [quantity, accrualMethod, number, startDate, endDate]).
inverse_of('includes', 'belongsTo').

'causes'(['Category'-'Category'], []).
inverse_of('causedBy', 'causes').

'classifiedAs'(['Category'-'Category', 'Word'-'Word'], []).
inverse_of('hasClassification', 'classifiedAs').

'clones'(['Document'-'Item', 'Artifact'-'Artifact', 'Document'-'Document', 'Component'-'Component', 'Device'-'Device', 'Software'-'Software', 'SoftwareComponent'-'SoftwareComponent'], []).
inverse_of('clonedBy', 'clones').

'concerns'(['Category'-'Category', 'Document'-'Artifact', 'Document'-'Document', 'Document'-'Event', 'Document'-'IntellectualWork', 'Document'-'InternetComponent', 'Document'-'Item', 'Document'-'Organization', 'Document'-'Person', 'Document'-'ProcessComponent', 'Artifact'-'Category', 'Artifact'-'Place', 'Artifact'-'TemporalSpecification', 'Document'-'Category', 'Document'-'Place', 'Document'-'TemporalSpecification', 'Edition'-'Category', 'Edition'-'Place', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'Category', 'IntellectualWork'-'Place', 'IntellectualWork'-'TemporalSpecification', 'Person'-'Category', 'Person'-'Place', 'Person'-'TemporalSpecification', 'Place'-'Category', 'Place'-'Place', 'Place'-'TemporalSpecification', 'PointOfInterest'-'Category', 'PointOfInterest'-'Place', 'PointOfInterest'-'TemporalSpecification', 'Document'-'Collection', 'Document'-'Component', 'Document'-'Device', 'Document'-'System', 'Document'-'Software', 'InformationItem'-'Collection', 'InformationItem'-'Component', 'InformationItem'-'Device', 'InformationItem'-'System', 'InformationItem'-'Software', 'ComponentDescription'-'ComponentDescription', 'Pseudocode'-'Pseudocode', 'SpecificationDocument'-'SpecificationDocument', 'UserStory'-'UserStory', 'BugReport'-'BugReport', 'TestCase'-'TestCase'], [position]).
inverse_of('citedIn', 'concerns').

'connectedTo'(['Place'-'Place'], [type]).
inverse_of('connectedTo', 'connectedTo').

'describes'(['Category'-'Nomen', 'ContentDescription'-'Artifact', 'ContentDescription'-'Device', 'ContentDescription'-'Document', 'ContentDescription'-'Event', 'ContentDescription'-'Person', 'ContentDescription'-'Place', 'ContentDescription'-'User', 'Person'-'Document', 'Place'-'Document', 'Place'-'User', 'Text'-'Category', 'Category'-'IntellectualWork', 'RequirementArtifact'-'Requirement', 'RequirementsDocument'-'ConceptualModel'], [degree, aspect]).
inverse_of('describedBy', 'describes').

'developed'(['Person'-'Artifact', 'Person'-'Document', 'Person'-'Event', 'Person'-'IntellectualWork', 'Person'-'Component', 'Person'-'Device', 'Person'-'Fix', 'Person'-'Collection', 'Person'-'Software', 'Person'-'System', 'Software'-'Stakeholder'], [role, order]).
inverse_of('developedBy', 'developed').

'entails'(['Category'-'Category'], []).
inverse_of('entailedBy', 'entails').

'evaluated'(['User'-'Artifact', 'User'-'Document', 'User'-'Item', 'Person'-'Artifact', 'Person'-'Document', 'Person'-'Component', 'Person'-'Device', 'Person'-'Item', 'Person'-'Package', 'Person'-'Software', 'Person'-'System'], [value, type, evaluationDate]).
inverse_of('evaluatedBy', 'evaluated').

'evolves'(['Artifact'-'Artifact', 'Document'-'Document', 'Edition'-'Edition', 'IntellectualWork'-'IntellectualWork', 'Component'-'Component', 'Device'-'Device', 'Software'-'Software', 'System'-'System', 'SoftwareComponent'-'SoftwareComponent'], []).
inverse_of('evolvedBy', 'evolves').

'expresses'(['Word'-'Person', 'Word'-'Place', 'Word'-'Category', 'Stakeholder'-'Intent'], [expressionDate]).
inverse_of('expressedBy', 'expresses').

'instanceOf'(['Category'-'Category', 'Document'-'IntellectualWork', 'Item'-'Device', 'Document'-'Edition', 'Edition'-'IntellectualWork', 'Item'-'Artifact', 'Item'-'Document', 'Place'-'Place', 'Software'-'IntellectualWork', 'SoftwareComponent'-'IntellectualWork'], []).
inverse_of('hasInstance', 'instanceOf').

'interactedWith'(['Person'-'Artifact', 'Person'-'Person', 'User'-'User', 'Person'-'Device', 'Person'-'Software', 'Stakeholder'-'Device', 'Stakeholder'-'Software'], [role, startDate, endDate]).
inverse_of('interactedWith', 'interactedWith').

'isA'(['Category'-'Category', 'Artifact'-'Artifact', 'Component'-'Component', 'Device'-'Device', 'Software'-'Software', 'SoftwareComponent'-'SoftwareComponent'], []).
inverse_of('hasSubclass', 'isA').

'knows'(['Person'-'Category', 'Person'-'Person'], [role]).
inverse_of('knownBy', 'knows').

'modified'(['Organization'-'Item', 'Person'-'Item', 'Artifact'-'TemporalSpecification', 'Document'-'TemporalSpecification', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'TemporalSpecification', 'Item'-'TemporalSpecification'], [date]).
inverse_of('modifiedBy', 'modified').

'nextTo'(['Place'-'Place'], []).
inverse_of('nextTo', 'nextTo').

'owned'(['Organization'-'Artifact', 'Organization'-'Document', 'Organization'-'Organization', 'Organization'-'Place', 'Person'-'Artifact', 'Person'-'Document', 'Person'-'Place', 'Organization'-'PointOfInterest', 'Person'-'PointOfInterest', 'Organization'-'Device', 'Organization'-'Item', 'Person'-'Component', 'Person'-'Configuration', 'Person'-'Device', 'Person'-'Item', 'Person'-'Package', 'Person'-'Software'], [price, quantity, startDate, endDate, public]).
inverse_of('ownedBy', 'owned').

'partOf'(['Document'-'Document', 'Text'-'Document', 'IntellectualWork'-'IntellectualWork', 'Organization'-'Organization', 'Text'-'Text', 'Category'-'Category', 'Category'-'Taxonomy', 'Document'-'Edition', 'Edition'-'Edition', 'Item'-'Artifact', 'TemporalSpecification'-'TemporalSpecification', 'Component'-'Item', 'Component'-'Device', 'Item'-'Device', 'Item'-'Document', 'Device'-'System', 'Software'-'Software', 'Software'-'SoftwareSystem', 'SoftwareComponents'-'Software', 'Program'-'SoftwareSystem', 'Code'-'Program', 'PerformedActivity'-'PerformedCompositeActivity', 'CodeDevelopment'-'Coding', 'CodeDocumentation'-'Coding'], [type, quantity, startDate, endDate]).
inverse_of('hasPart', 'partOf').

'pertainsTo'(['Word'-'Word'], []).
inverse_of('pertainedBy', 'pertainsTo').

'precedes'(['IntellectualWork'-'IntellectualWork', 'Organization'-'Organization'], []).
inverse_of('succeeds', 'precedes').

'produced'(['Organization'-'Artifact', 'Organization'-'Document', 'Organization'-'Collection', 'Organization'-'Component', 'Organization'-'Device', 'Organization'-'Package', 'Organization'-'Series', 'Organization'-'Software', 'Organization'-'System'], [presentationDate, startDate, endDate, role, place]).
inverse_of('producedBy', 'produced').

'relatedTo'(['Category'-'Category'], []).
inverse_of('relatedTo', 'relatedTo').

'relevantFor'(['Document'-'Category', 'Event'-'Category', 'Nomen'-'Category', 'Place'-'Category', 'Place'-'IntellectualWork', 'Person'-'Category', 'Category'-'Event', 'Category'-'Person', 'Category'-'Place', 'Category'-'User', 'Category'-'Collection', 'Category'-'PointOfInterest', 'IntellectualWork'-'Category', 'Person'-'PointOfInterest', 'PointOfInterest'-'Category'], [reason, startDate, endDate, range, role]).
inverse_of('pertains', 'relevantFor').

'requires'(['IntellectualWork'-'IntellectualWork', 'Software'-'Device', 'Software'-'Software'], []).
inverse_of('requiredBy', 'requires').

'seeAlso'(['Word'-'Word'], []).
inverse_of('seeAlso', 'seeAlso').

'similarMeaningAs'(['Category'-'Category'], []).
inverse_of('similarMeaningAs', 'similarMeaningAs').

'similarTo'(['Category'-'Category'], []).
inverse_of('similarTo', 'similarTo').

'used'(['Document'-'Language', 'Language'-'Nomen', 'Language'-'Word', 'Organization'-'Language', 'Place'-'Language', 'Text'-'Language', 'Expression'-'Language', 'MusicalWork'-'Category'], [role, reason, startDate, endDate]).
inverse_of('usedBy', 'used').

'wasIn'(['Artifact'-'Event', 'Collection'-'Event', 'Collection'-'Place', 'Organization'-'Event', 'Event'-'Event', 'Person'-'Event', 'User'-'Event', 'User'-'User', 'Organization'-'Place', 'Document'-'Document', 'Document'-'Event', 'Document'-'Place', 'Event'-'Place', 'Person'-'Organization', 'Person'-'Place', 'PointOfInterest'-'Place', 'Place'-'Place', 'Component'-'Event', 'Device'-'Event', 'Item'-'Event', 'Item'-'Place', 'Person'-'Collection', 'Place'-'Collection', 'System'-'Event', 'Stakeholder'-'TeamStakeholder', 'TeamStakeholder'-'Organization'], [role, address, reason, position, startDate, endDate]).
inverse_of('hosted', 'wasIn').

'acquired'(['Organization'-'Organization'], [date, originalPrice]).
inverse_of('acquiredBy', 'acquired').

'aggregated'(['Expression'-'Expression'], []).
inverse_of('aggregatedBy', 'aggregated').

'adopts'(['Artifact'-'Document', 'Country'-'Language', 'Edition'-'Document', 'Edition'-'Language', 'IntellectualWork'-'Document', 'IntellectualWork'-'Language'], []).
inverse_of('adoptedBy', 'adopts').

'available'(['Artifact'-'TemporalSpecification', 'Document'-'TemporalSpecification', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'TemporalSpecification', 'Item'-'TemporalSpecification'], []).
inverse_of('availabilityOf', 'available').

'branchOf'(['Organization'-'Organization'], []).
inverse_of('controls', 'branchOf').

'cites'(['Document'-'Artifact', 'Document'-'Category', 'Document'-'Document', 'Document'-'Edition', 'Document'-'IntellectualWork', 'Document'-'Item', 'Document'-'Organization', 'Document'-'Person', 'Document'-'Place'], [position]).
inverse_of('citedIn', 'cites').

'contributorOf'(['Organization'-'Artifact', 'Organization'-'Document', 'Organization'-'Edition', 'Organization'-'IntellectualWork', 'Organization'-'Item', 'Organization'-'Collection', 'Person'-'Artifact', 'Person'-'Document', 'Person'-'Edition', 'Person'-'IntellectualWork', 'Person'-'Item'], [role, place, startDate, endDate, order]).
inverse_of('hasContributor', 'contributorOf').

'derivesFrom'(['IntellectualWork'-'IntellectualWork'], []).
inverse_of('spawned', 'derivesFrom').

'hasAlternate'(['Document'-'Document'], []).
inverse_of('isAlternateOf', 'hasAlternate').

'influenced'(['IntellectualWork'-'IntellectualWork'], []).
inverse_of('influencedBy', 'influenced').

'packagedWith'(['Software'-'Document', 'Device'-'Device', 'Document'-'Device', 'SoftwareComponent'-'InformationItem'], []).
inverse_of('hadInPackage', 'packagedWith').

'references'(['Edition'-'Document', 'Edition'-'Edition', 'Edition'-'IntellectualWork', 'Edition'-'Item', 'IntellectualWork'-'Document', 'IntellectualWork'-'Edition', 'IntellectualWork'-'IntellectualWork', 'IntellectualWork'-'Item'], []).
inverse_of('referencedBy', 'references').

'updated'(['Artifact'-'TemporalSpecification', 'Document'-'TemporalSpecification', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'TemporalSpecification', 'Item'-'TemporalSpecification'], []).
inverse_of('updatingOf', 'updated').

'unavailable'(['Artifact'-'TemporalSpecification', 'Document'-'TemporalSpecification', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'TemporalSpecification', 'Item'-'TemporalSpecification'], []).
inverse_of('unavailabilityOf', 'unavailable').

'valid'(['Artifact'-'TemporalSpecification', 'Document'-'TemporalSpecification', 'Edition'-'TemporalSpecification', 'IntellectualWork'-'TemporalSpecification', 'Item'-'TemporalSpecification'], []).
inverse_of('validityOf', 'valid').

'compatibleWith'(['Component'-'Component', 'Device'-'Device', 'Software'-'Software', 'SoftwareComponent'-'SoftwareComponent'], []).
inverse_of('compatibleWith', 'compatibleWith').

'got'(['Organization'-'Award', 'Document'-'Award', 'Event'-'Award', 'IntellectualWork'-'Award', 'Person'-'Award', 'Place'-'Award', 'Software'-'Award'], [date, mark, rank]).
inverse_of('givenTo', 'got').

'mayReplace'(['Component'-'Component', 'SoftwareComponent'-'SoftwareComponent'], []).
inverse_of('mayBeReplacedBy', 'mayReplace').

'proposed'(['Package'-'Configuration'], []).
inverse_of('proposedBy', 'proposed').

'repaired'(['Person'-'Item'], [reason, date]).
inverse_of('repairedBy', 'repaired').

'assignedTo'(['BugReport'-'Stakeholder'], [assignmentDate, priority, status]).
inverse_of('assignedBy', 'assignedTo').

'follows'(['Software'-'Software'], []).
inverse_of('followedBy', 'follows').

'distributes'(['Software'-'Software'], []).
inverse_of('distributedBy', 'distributes').

'isEdition'(['Software'-'Software'], []).
inverse_of('hasEdition', 'isEdition').

'creates'(['CodeDevelopment'-'Code', 'ConceptualModeling'-'ConceptualModel', 'RequirementsElicitation'-'RequirementArtifact', 'RequirementsDocument'-'RequirementsDocument', 'RequirementsVerification'-'RequirementsEvaluationDocument', 'RequirementsNegotiation'-'RequirementsAgreement', 'CodeReview'-'CodeReviewReport', 'PerformedActivity'-'Artifact'], [creationDate]).
inverse_of('createdBy', 'creates').

'dependsOn'(['PerformedActivity'-'PerformedActivity'], []).
inverse_of('dependencyOf', 'dependsOn').

'derivedFrom'(['TestCase'-'UseCase'], []).
inverse_of('derives', 'derivedFrom').

'executableOn'(['Software'-'OperatingSystem'], []).
inverse_of('executes', 'executableOn').

'formalizes'(['Requirement'-'Intent'], []).
inverse_of('formalizedBy', 'formalizes').

'forwardedTo'(['AgreementEmail'-'AgreementEmail'], [forwardingDate]).
inverse_of('forwardedBy', 'forwardedTo').

'generates'(['SourceCode'-'MachineCode'], []).
inverse_of('generatedFrom', 'generates').

'implements'(['SoftwareSystem'-'SystemSpecification', 'Program'-'ProgramSpecification', 'Code'-'Algorithm'], []).
inverse_of('implementedBy', 'implements').

'inChargeOf'(['RequirementsReviewer'-'RequirementsNegotiation', 'Programmer'-'Coding', 'CodeReviewer'-'CodeReview', 'RequirementsEngineer'-'RequirementsDocumentation', 'RequirementsEngineer'-'ConceptualModeling', 'RequirementsEngineer'-'RequirementsElicitation', 'Stakeholder'-'PerformedActivity', 'Stakeholder'-'Artifact'], [startDate, endDate]).
inverse_of('managedBy', 'inChargeOf').

'inDirectory'(['Software'-'Directory', 'InformationItem'-'Directory'], [addedDate]).
inverse_of('contains', 'inDirectory').

'installedIn'(['Software'-'Server'], [installationDate, installationPath]).
inverse_of('hosts', 'installedIn').

'participatesIn'(['Stakeholder'-'PerformedActivity', 'RequirementsStakeholder'-'RequirementsElicitation', 'RequirementsStakeholder'-'RequirementsVerification', 'RequirementsStakeholder'-'RequirementsNegotiation', 'RequirementsEngineer'-'RequirementsVerification'], [startDate, endDate]).
inverse_of('involves', 'participatesIn').

'refersTo'(['SpecificationDocument'-'SpecificationDocument'], []).
inverse_of('referredBy', 'refersTo').

'runnedOn'(['OperatingSystem'-'Server'], []).
inverse_of('runs', 'runnedOn').

'satisfies'(['SystemSpecification'-'Intent', 'ProgramSpecification'-'SystemSpecification'], []).
inverse_of('satisfiedBy', 'satisfies').

'writtenIn'(['SourceCode'-'ProgrammingLanguage', 'Software'-'ProgrammingLanguage'], []).
inverse_of('usedBy', 'writtenIn').


% GENERIC RULES

%% is_subclass(?Subclass, ?Superclass)
%
% Predicate to check if Subclass is a subclass at any hierarchy level of Superclass.
% This will work for any GraphBrain type, let it be entities, relationships or inverse relationships

is_subclass(Subclass, Superclass) :-
	is_subclass_normal(Subclass, Superclass).

is_subclass(Subclass, Superclass) :-
	\+ is_subclass_normal(Subclass, _),
	is_subclass_inverse(Subclass, Superclass).


% is_subclass_normal(?Subclass, ?Superclass)
%
% Predicate to check if Subclass is a subclass at any hierarchy level of Superclass.
% This won't work for inverse relationships

is_subclass_normal(Subclass, Superclass) :-
	subclass_of(Subclass, Superclass).

is_subclass_normal(Subclass, Superclass) :-
	subclass_of(Subclass, Middleclass),
	is_subclass_normal(Middleclass, Superclass).


% is_subclass_inverse(?SubclassInverse, ?SuperclassInverse)
%
% Predicate to check if SubclassInverse is a subclass at any hierarchy level of SuperclassInverse.
% This will ONLY work for inverse relationships, and it is typically used to retrieve all
% parents/children of an inverse relationship

is_subclass_inverse(SubclassInverse, SuperclassInverse) :-
	invert_relationship(SubclassInverse, SubclassClause),
	SubclassClause =.. [SubclassName|_],
	is_subclass_normal(SubclassName, SuperclassName),
	invert_relationship(SuperclassName, SuperclassClauseInverse),
	SuperclassClauseInverse =.. [SuperclassInverse|_].


%% invert_relationship(+RelationshipName, -InvertedRelationshipClause)
%
% Predicate which, given a RelationshipName returns its inverse relationship clause.
% This predicate is reflexive (i.e. given an inverted relationship name, the output will be the original
% relationship clause).
% If the relationship has no inverse, the output clause is the clause of the input relationship itself.

% we are inverting a "normal" relationship
invert_relationship(RelationshipName, InvertedRelationshipClause) :-
	inverse_of(InvertedRelationshipName, RelationshipName),
	!,
	RelationshipToInvert =.. [RelationshipName, SubjObjList, AttributeList],
	call(RelationshipToInvert),
	invert_subj_obj(SubjObjList, InvertedSubjObjList),
	InvertedRelationshipClause =.. [InvertedRelationshipName, InvertedSubjObjList, AttributeList].

% we are inverting an "inverted" relationship
invert_relationship(InvertedRelationshipName, RelationshipClause) :-
	inverse_of(InvertedRelationshipName, RelationshipName),
	RelationshipClause =.. [RelationshipName, _SubjObjList, _Attributes],
	call(RelationshipClause),
	!.

% we are inverting a relationship with no inverse
invert_relationship(RelationshipName, Relationship) :-
	Relationship =.. [RelationshipName, _SubjObjList, _AttributeList],
	call(Relationship).


% invert_subj_obj(+SubjectObjectList, -ObjectSubjectList)
%
% Predicate which inverts all references of a relationship

invert_subj_obj([], []).

invert_subj_obj([Subject-Object|T1], [Object-Subject|T2]) :-
	invert_subj_obj(T1, T2).


%% gather_attributes(+SubC, -Attributes)
%
% Predicate which will gather all attributes of SubC + all attributes of its parents classes in its
% taxonomy. The order of the attributes is based on the "distance" of the parent class from SubC in its
% hierarchy: the attributes of the furthest parent class will be shown first.

% for entities
gather_attributes(SubC, Attributes) :-
	Clause =.. [SubC, SubCAttributes],
	call(Clause),
	!,
	findall(SuperC, is_subclass(SubC, SuperC), ParentsList),
	gather_parent_attributes(ParentsList, ParentAttributes),
	reverse(ParentAttributes, ReversedParentAttributes),
	flatten([ReversedParentAttributes|SubCAttributes], Attributes).

% for relationships
gather_attributes(SubC, Attributes) :-
	Clause =.. [SubC, _SubCReferences, SubCAttributes],
	call(Clause),
	!, % it means we are processing a normal relationship (i.e. not inversed)
	findall(SuperC, is_subclass(SubC, SuperC), ParentList),
	gather_parent_attributes(ParentList, ParentAttributes),
	reverse(ParentAttributes, ReversedParentAttributes),
	flatten([ReversedParentAttributes|SubCAttributes], Attributes).

gather_attributes(InverseSubC, Attributes) :-
	inverse_of(InverseSubC, SubC),
	gather_attributes(SubC, Attributes).


% gather_parent_attributes(+ParentClasses, -ParentAttributes)
%
% Predicate which will gather, for each class in the ParentClasses argument, all of their attributes.
% The order of the attributes depends on the ParentClasses ordering

gather_parent_attributes([], []).

% for entities
gather_parent_attributes([ImmediateParentC|ParentClasses], [ImmediateParentCAttributes|ParentAttributes]) :-
	Clause =.. [ImmediateParentC, ImmediateParentCAttributes],
	call(Clause),
	!,
	gather_parent_attributes(ParentClasses, ParentAttributes).

% for relationships
gather_parent_attributes([ImmediateParentC|ParentClasses], [ImmediateParentCAttributes|ParentAttributes]) :-
	Clause =.. [ImmediateParentC, _ParentCReferences, ImmediateParentCAttributes],
	call(Clause),
	gather_parent_attributes(ParentClasses, ParentAttributes).


%% gather_references(+SubC, -References)
%
% Predicate which will gather all references of SubC + all references of its parents classes in its
% taxonomy. The order of the references is based on the "distance" of the parent class from SubC in its
% hierarchy: the references of the furthest parent class will be shown first.

gather_references(SubC, References) :-
	Clause =.. [SubC, SubCReferences, _SubCAttributes],
	call(Clause),
	!, % it means we are processing a normal relationship (i.e. not inversed)
	findall(SuperC, is_subclass(SubC, SuperC), ParentsList),
	gather_parent_references(ParentsList, ParentReferences),
	reverse(ParentReferences, ReversedParentReferences),
	flatten([ReversedParentReferences|SubCReferences], References).

gather_references(InverseSubC, InvertedReferences) :-
	inverse_of(InverseSubC, SubC),
	gather_references(SubC, References),
	invert_subj_obj(References, InvertedReferences).


% gather_parent_reference(+ParentClasses, -ParentReferences)
%
% Predicate which will gather, for each class in the ParentClasses argument, all of their references.
% The order of the references depends on the ParentClasses ordering

gather_parent_references([], []).

gather_parent_references([ImmediateParentC|ParentClasses], [ImmediateParentCReferences|ParentCReferences]) :-
	Clause =.. [ImmediateParentC, ImmediateParentCReferences, _],
	call(Clause),
	gather_parent_references(ParentClasses, ParentCReferences).
