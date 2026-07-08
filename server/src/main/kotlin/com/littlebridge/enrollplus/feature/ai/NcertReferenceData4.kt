package com.littlebridge.enrollplus.feature.ai

import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertChapter
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertSyllabus
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertSubtopic
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertTopic

object NcertReferenceData4 {

    val DATA: List<NcertSyllabus> = listOf(

        // ════ CLASS 3 ════
        NcertSyllabus("Class 3", "Mathematics", listOf(
            NcertChapter("Where to Look From", listOf(
                NcertTopic("Different Views of Objects"),
                NcertTopic("Symmetry", listOf(NcertSubtopic("Line of Symmetry"))),
            )),
            NcertChapter("Fun with Numbers", listOf(
                NcertTopic("Number Puzzles", listOf(NcertSubtopic("Number Series"), NcertSubtopic("Missing Numbers"))),
                NcertTopic("Addition and Subtraction Games"),
            )),
            NcertChapter("Give and Take", listOf(
                NcertTopic("Addition", listOf(NcertSubtopic("Three-Digit Addition"), NcertSubtopic("Carrying Over"))),
                NcertTopic("Subtraction", listOf(NcertSubtopic("Three-Digit Subtraction"), NcertSubtopic("Borrowing"))),
            )),
            NcertChapter("Long and Short", listOf(
                NcertTopic("Measuring Length", listOf(NcertSubtopic("Centimetres and Metres"), NcertSubtopic("Kilometres"))),
                NcertTopic("Estimating Distance"),
            )),
            NcertChapter("Shapes and Designs", listOf(
                NcertTopic("2D Shapes", listOf(NcertSubtopic("Triangle, Square, Rectangle"), NcertSubtopic("Sides and Corners"))),
                NcertTopic("3D Shapes", listOf(NcertSubtopic("Cube, Cuboid, Sphere"), NcertSubtopic("Faces, Edges, Vertices"))),
                NcertTopic("Patterns in Shapes"),
            )),
            NcertChapter("Fun with Give and Take", listOf(
                NcertTopic("Word Problems", listOf(NcertSubtopic("Addition Stories"), NcertSubtopic("Subtraction Stories"))),
            )),
            NcertChapter("Time Goes On", listOf(
                NcertTopic("Reading Clock", listOf(NcertSubtopic("Hours and Minutes"), NcertSubtopic("Half Past, Quarter Past"))),
                NcertTopic("Calendar", listOf(NcertSubtopic("Days, Weeks, Months"), NcertSubtopic("Reading Dates"))),
            )),
            NcertChapter("Who is Heavier?", listOf(
                NcertTopic("Weight", listOf(NcertSubtopic("Grams and Kilograms"), NcertSubtopic("Comparing Weights"))),
            )),
            NcertChapter("How Many Times?", listOf(
                NcertTopic("Multiplication", listOf(NcertSubtopic("Repeated Addition"), NcertSubtopic("Multiplication Tables 2 to 10"), NcertSubtopic("Word Problems"))),
            )),
            NcertChapter("Play with Patterns", listOf(
                NcertTopic("Number Patterns"),
                NcertTopic("Shape Patterns"),
                NcertTopic("Odd and Even Numbers"),
            )),
            NcertChapter("Jugs and Mugs", listOf(
                NcertTopic("Capacity", listOf(NcertSubtopic("Litres and Millilitres"), NcertSubtopic("Comparing Capacity"))),
            )),
            NcertChapter("Can We Share?", listOf(
                NcertTopic("Division", listOf(NcertSubtopic("Sharing Equally"), NcertSubtopic("Division as Repeated Subtraction"))),
            )),
            NcertChapter("Smart Charts", listOf(
                NcertTopic("Data Handling", listOf(NcertSubtopic("Pictographs"), NcertSubtopic("Bar Charts"))),
            )),
            NcertChapter("Rupees and Paise", listOf(
                NcertTopic("Money", listOf(NcertSubtopic("Indian Currency"), NcertSubtopic("Adding and Subtracting Money"), NcertSubtopic("Making Change"))),
            )),
        )),

        NcertSyllabus("Class 3", "English", listOf(
            NcertChapter("Good Morning (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("New Words"))),
            NcertChapter("The Magic Garden", listOf(NcertTopic("Story Reading"), NcertTopic("Comprehension"))),
            NcertChapter("Bird Talk (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Birds"))),
            NcertChapter("Nina and the Baby Sparrows", listOf(NcertTopic("Story Reading"), NcertTopic("Kindness to Animals"))),
            NcertChapter("Little by Little (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Trees and Growth"))),
            NcertChapter("The Enormous Turnip", listOf(NcertTopic("Story Reading"), NcertTopic("Folktale"))),
            NcertChapter("Sea Song (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("A Little Fish Story", listOf(NcertTopic("Story Reading"), NcertTopic("Moral Values"))),
            NcertChapter("The Balloon Man (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("The Yellow Butterfly", listOf(NcertTopic("Story Reading"), NcertTopic("Nature"))),
            NcertChapter("Trains (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Transport"))),
            NcertChapter("The Story of the Road", listOf(NcertTopic("Story Reading"), NcertTopic("Comprehension"))),
            NcertChapter("Puppy and I (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Animals"))),
            NcertChapter("Little Tiger, Big Tiger", listOf(NcertTopic("Story Reading"), NcertTopic("Wildlife"))),
            NcertChapter("What's in the Mailbox?", listOf(NcertTopic("Reading"), NcertTopic("Letters and Communication"))),
            NcertChapter("My Silly Sister (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Don't Tell (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("He is My Brother", listOf(NcertTopic("Story Reading"), NcertTopic("Family Bonds"))),
            NcertChapter("How Creatures Move (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Animal Movement"))),
            NcertChapter("The Ship of the Desert", listOf(NcertTopic("Story Reading"), NcertTopic("Camel and Desert"))),
        )),

        NcertSyllabus("Class 3", "Hindi", listOf(
            NcertChapter("कक्कू (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("नए शब्द"))),
            NcertChapter("शेर की दाढ़ी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रश्नोत्तर"))),
            NcertChapter("बहादुर बित्तो (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("वीरता"))),
            NcertChapter("हम सब सुंदर हैं", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("सुंदरता और विविधता"))),
            NcertChapter("मीठी सारंगी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("संगीत"))),
            NcertChapter("रुम-झुम-रुम-झुम", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("बारिश"))),
            NcertChapter("प्यारा गाँव (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("गाँव का वर्णन"))),
            NcertChapter("तितली और कली (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("प्रकृति"))),
            NcertChapter("अक्ल बड़ी या भैंस", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("बुद्धि और शक्ति"))),
            NcertChapter("क्योंजी और मोर (कविता)", listOf(NcertTopic("कविता वाचन"))),
            NcertChapter("जल ही जल है", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("जल का महत्व"))),
            NcertChapter("बाघ और बिल्ली", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("नैतिक कहानी"))),
            NcertChapter("शीशा और मोर (कविता)", listOf(NcertTopic("कविता वाचन"))),
            NcertChapter("चींटी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("कठिन परिश्रम"))),
        )),

        NcertSyllabus("Class 3", "EVS", listOf(
            NcertChapter("Poonam's Day out", listOf(NcertTopic("Daily Routine"), NcertTopic("Animals and Their Sounds"))),
            NcertChapter("The Plant Fairy", listOf(
                NcertTopic("Plants", listOf(NcertSubtopic("Types of Plants"), NcertSubtopic("Leaves - Shapes and Types"))),
                NcertTopic("Protecting Plants"),
            )),
            NcertChapter("Water O Water!", listOf(
                NcertTopic("Sources of Water", listOf(NcertSubtopic("River, Pond, Well, Tap"))),
                NcertTopic("Clean Water"),
                NcertTopic("Water Conservation"),
            )),
            NcertChapter("Our First School", listOf(NcertTopic("Family as First School"), NcertTopic("Learning at Home"))),
            NcertChapter("Chhotu's House", listOf(
                NcertTopic("Types of Houses", listOf(NcertSubtopic("Pucca House"), NcertSubtopic("Kutcha House"), NcertSubtopic("Houseboat, Tent"))),
            )),
            NcertChapter("Foods We Eat", listOf(
                NcertTopic("Different Foods", listOf(NcertSubtopic("Food in Different Regions"), NcertSubtopic("Raw and Cooked Food"))),
                NcertTopic("Balanced Diet"),
            )),
            NcertChapter("Saying Without Speaking", listOf(
                NcertTopic("Non-Verbal Communication", listOf(NcertSubtopic("Gestures and Expressions"), NcertSubtopic("Sign Language"))),
            )),
            NcertChapter("Flying High", listOf(
                NcertTopic("Birds", listOf(NcertSubtopic("Types of Birds"), NcertSubtopic("Beaks and Claws"), NcertSubtopic("Nests"))),
            )),
            NcertChapter("It's Raining", listOf(NcertTopic("Rain and Rainwater"), NcertTopic("Animals in Rain"))),
            NcertChapter("What is Cooking?", listOf(
                NcertTopic("Utensils Used in Cooking"),
                NcertTopic("Methods of Cooking", listOf(NcertSubtopic("Boiling, Frying, Baking"))),
            )),
            NcertChapter("From Here to There", listOf(
                NcertTopic("Means of Transport", listOf(NcertSubtopic("Bullock Cart, Bicycle"), NcertSubtopic("Bus, Train, Aeroplane"))),
            )),
            NcertChapter("Work We Do", listOf(
                NcertTopic("Different Occupations", listOf(NcertSubtopic("Farmer, Teacher, Doctor"), NcertSubtopic("Cobbler, Barber, Tailor"))),
                NcertTopic("Respect for All Work"),
            )),
            NcertChapter("Sharing Our Feelings", listOf(NcertTopic("Expressing Emotions"), NcertTopic("Helping Others"))),
            NcertChapter("The Story of Food", listOf(
                NcertTopic("Where Food Comes From", listOf(NcertSubtopic("From Plants"), NcertSubtopic("From Animals"))),
            )),
            NcertChapter("Making Pots", listOf(NcertTopic("Clay and Pottery"), NcertTopic("Materials Used"))),
            NcertChapter("Games We Play", listOf(
                NcertTopic("Indoor Games", listOf(NcertSubtopic("Chess, Carrom"))),
                NcertTopic("Outdoor Games", listOf(NcertSubtopic("Cricket, Football, Kabaddi"))),
            )),
            NcertChapter("Here Comes a Letter", listOf(NcertTopic("Post Office"), NcertTopic("Address on a Letter"))),
            NcertChapter("A House Like This", listOf(
                NcertTopic("Houses in Different Regions", listOf(NcertSubtopic("House in Mountains"), NcertSubtopic("House in Desert"), NcertSubtopic("House in Forest"))),
            )),
            NcertChapter("Our Friends - Animals", listOf(NcertTopic("Caring for Animals"), NcertTopic("Animal Rescue"))),
            NcertChapter("Drop by Drop", listOf(NcertTopic("Water Scarcity"), NcertTopic("Rainwater Harvesting"))),
            NcertChapter("Families Can Be Different", listOf(
                NcertTopic("Types of Families", listOf(NcertSubtopic("Nuclear Family"), NcertSubtopic("Joint Family"))),
            )),
            NcertChapter("Left-Right", listOf(
                NcertTopic("Directions", listOf(NcertSubtopic("Left and Right"), NcertSubtopic("Map Reading Basics"))),
            )),
        )),

        // ════ CLASS 4 ════
        NcertSyllabus("Class 4", "Mathematics", listOf(
            NcertChapter("Building with Bricks", listOf(NcertTopic("Patterns in Bricks"), NcertTopic("Symmetry in Designs"))),
            NcertChapter("Long and Short", listOf(
                NcertTopic("Length", listOf(NcertSubtopic("Metres and Kilometres"), NcertSubtopic("Conversion of Units"))),
                NcertTopic("Estimating Distance"),
            )),
            NcertChapter("A Trip to Bhopal", listOf(
                NcertTopic("Addition and Subtraction", listOf(NcertSubtopic("Four-Digit Numbers"), NcertSubtopic("Word Problems"))),
                NcertTopic("Multiplication in Real Life"),
            )),
            NcertChapter("Tick-Tick-Tick", listOf(
                NcertTopic("Time", listOf(NcertSubtopic("Reading Clock - Hours and Minutes"), NcertSubtopic("AM and PM"), NcertSubtopic("Time Duration"))),
                NcertTopic("Calendar", listOf(NcertSubtopic("Leap Year"))),
            )),
            NcertChapter("The Way The World Looks", listOf(
                NcertTopic("Views of Objects", listOf(NcertSubtopic("Top View, Side View, Front View"))),
                NcertTopic("Spatial Understanding"),
            )),
            NcertChapter("The Junk Seller", listOf(
                NcertTopic("Multiplication", listOf(NcertSubtopic("Multiplying Larger Numbers"), NcertSubtopic("Word Problems"))),
                NcertTopic("Money Calculations"),
            )),
            NcertChapter("Jugs and Mugs", listOf(
                NcertTopic("Capacity", listOf(NcertSubtopic("Litres and Millilitres"), NcertSubtopic("Estimating Capacity"))),
            )),
            NcertChapter("Carts and Wheels", listOf(
                NcertTopic("Circles", listOf(NcertSubtopic("Radius and Centre"), NcertSubtopic("Drawing Circles"))),
            )),
            NcertChapter("Halves and Quarters", listOf(
                NcertTopic("Fractions", listOf(NcertSubtopic("Half, Quarter, Three-Fourth"), NcertSubtopic("Equivalent Fractions"), NcertSubtopic("Comparing Fractions"))),
            )),
            NcertChapter("Play with Patterns", listOf(
                NcertTopic("Number Patterns", listOf(NcertSubtopic("Even and Odd"), NcertSubtopic("Multiples"))),
                NcertTopic("Shape Patterns"),
            )),
            NcertChapter("Tables and Shares", listOf(
                NcertTopic("Division", listOf(NcertSubtopic("Division by Grouping"), NcertSubtopic("Remainder"), NcertSubtopic("Word Problems"))),
                NcertTopic("Multiplication Tables"),
            )),
            NcertChapter("How Heavy? How Light?", listOf(
                NcertTopic("Weight", listOf(NcertSubtopic("Grams and Kilograms"), NcertSubtopic("Estimating Weight"), NcertSubtopic("Conversion"))),
            )),
            NcertChapter("Fields and Fences", listOf(
                NcertTopic("Perimeter", listOf(NcertSubtopic("Perimeter of Rectangle"), NcertSubtopic("Perimeter of Square"))),
            )),
            NcertChapter("Smart Charts", listOf(
                NcertTopic("Data Handling", listOf(NcertSubtopic("Tally Marks"), NcertSubtopic("Pie Charts Introduction"))),
            )),
        )),

        NcertSyllabus("Class 4", "English", listOf(
            NcertChapter("Wake Up! (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Nature and Morning"))),
            NcertChapter("Neha's Alarm Clock", listOf(NcertTopic("Story Reading"), NcertTopic("Time and Habits"))),
            NcertChapter("Noses (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Body Parts"))),
            NcertChapter("The Little Fir Tree", listOf(NcertTopic("Story Reading"), NcertTopic("Contentment"))),
            NcertChapter("Run! (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Nasruddin's Aim", listOf(NcertTopic("Story Reading"), NcertTopic("Humour"))),
            NcertChapter("Why? (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Curiosity"))),
            NcertChapter("The Scholar's Mother Tongue", listOf(NcertTopic("Story Reading"), NcertTopic("Wisdom"))),
            NcertChapter("A Watering Rhyme (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Gardening"))),
            NcertChapter("The Donkey", listOf(NcertTopic("Story Reading"), NcertTopic("Idea and Execution"))),
            NcertChapter("Books (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Reading Habit"))),
            NcertChapter("I Had a Little Pony", listOf(NcertTopic("Story Reading"), NcertTopic("Kindness to Animals"))),
            NcertChapter("Hiawatha (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Nature and Animals"))),
            NcertChapter("The Milkman's Cow", listOf(NcertTopic("Story Reading"), NcertTopic("Compassion"))),
            NcertChapter("Don't Be Afraid of the Dark (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Courage"))),
            NcertChapter("The Man Who Knew Too Much", listOf(NcertTopic("Story Reading"), NcertTopic("Wisdom"))),
        )),

        NcertSyllabus("Class 4", "Hindi", listOf(
            NcertChapter("मन के भोले-भाले बादल (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("बादल और प्रकृति"))),
            NcertChapter("जैसा सवाल वैसा जवाब", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("बुद्धि और चतुराई"))),
            NcertChapter("किताबें बच्चों की (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("पुस्तकें"))),
            NcertChapter("देवका का बंदर", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("नैतिक शिक्षा"))),
            NcertChapter("थीम भारत देश (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("देश प्रेम"))),
            NcertChapter("जो देखकर भी नहीं देखते", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("ध्यान और सावधानी"))),
            NcertChapter("सुनीता की पहिया कुर्सी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("साहस और प्रेरणा"))),
            NcertChapter("हुदहुद (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("पक्षी"))),
            NcertChapter("सुंदर भारत (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("देश की सुंदरता"))),
            NcertChapter("बढ़ते चलो (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("प्रगति"))),
            NcertChapter("तब नाव चली (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("साहस"))),
            NcertChapter("पानी फिर भी बचा (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("जल संरक्षण"))),
            NcertChapter("झाँसी की रानी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("वीरता"))),
            NcertChapter("पढ़ने वाली दीदी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रेरणा"))),
        )),

        NcertSyllabus("Class 4", "EVS", listOf(
            NcertChapter("Going to School", listOf(
                NcertTopic("Different Ways to Reach School", listOf(NcertSubtopic("Walking, Bus, Bicycle"), NcertSubtopic("Boat, Rope Way"))),
            )),
            NcertChapter("Ear to Ear", listOf(
                NcertTopic("Animals and Their Ears", listOf(NcertSubtopic("Shape of Ears"), NcertSubtopic("Hearing"))),
                NcertTopic("Animals That Lay Eggs vs Give Birth"),
            )),
            NcertChapter("A Day with Nandu", listOf(
                NcertTopic("Elephants", listOf(NcertSubtopic("Elephant Herd"), NcertSubtopic("Elephant Body"))),
                NcertTopic("Animal Suffering and Care"),
            )),
            NcertChapter("The Story of Amrita", listOf(
                NcertTopic("Trees and Their Importance", listOf(NcertSubtopic("Chipko Movement"), NcertSubtopic("Protecting Trees"))),
            )),
            NcertChapter("Anita and the Honeybees", listOf(
                NcertTopic("Honeybees", listOf(NcertSubtopic("Beehive"), NcertSubtopic("Honey Making"))),
                NcertTopic("Beekeeping as a Livelihood"),
            )),
            NcertChapter("Omana's Journey", listOf(
                NcertTopic("Train Journey", listOf(NcertSubtopic("Tickets and Platforms"), NcertSubtopic("Scenes from Train"))),
            )),
            NcertChapter("From the Window", listOf(
                NcertTopic("Observing from a Moving Vehicle"),
                NcertTopic("Different Landscapes"),
            )),
            NcertChapter("Reaching Grandmother's House", listOf(
                NcertTopic("Long Distance Travel", listOf(NcertSubtopic("Bus, Train, Aeroplane"))),
            )),
            NcertChapter("Changing Families", listOf(
                NcertTopic("Family Changes", listOf(NcertSubtopic("New Members"), NcertSubtopic("Moving Places"))),
            )),
            NcertChapter("Hu Tu Tu, Hu Tu Tu", listOf(
                NcertTopic("Kabaddi", listOf(NcertSubtopic("Rules of Kabaddi"))),
                NcertTopic("Team Games"),
            )),
            NcertChapter("The Valley of Flowers", listOf(
                NcertTopic("Flowers", listOf(NcertSubtopic("Types of Flowers"), NcertSubtopic("Flowers in Different Seasons"))),
            )),
            NcertChapter("Changing Times", listOf(
                NcertTopic("How Things Change", listOf(NcertSubtopic("Old vs New"))),
            )),
            NcertChapter("A River's Tale", listOf(
                NcertTopic("Rivers", listOf(NcertSubtopic("Source of River"), NcertSubtopic("Pollution in Rivers"))),
            )),
            NcertChapter("Basva's Farm", listOf(
                NcertTopic("Farming", listOf(NcertSubtopic("Ploughing"), NcertSubtopic("Sowing Seeds"), NcertSubtopic("Harvesting"))),
            )),
            NcertChapter("From Market to Home", listOf(
                NcertTopic("Market", listOf(NcertSubtopic("Types of Markets"), NcertSubtopic("Buying Vegetables"))),
            )),
            NcertChapter("A Busy Month", listOf(
                NcertTopic("Birds and Nests", listOf(NcertSubtopic("Nest Building"), NcertSubtopic("Caring for Young"))),
            )),
            NcertChapter("Nandita in Mumbai", listOf(
                NcertTopic("City Life", listOf(NcertSubtopic("Flat, Chawl"), NcertSubtopic("Crowded Places"))),
            )),
            NcertChapter("Too Much Water, Too Little Water", listOf(
                NcertTopic("Water Problems", listOf(NcertSubtopic("Floods"), NcertSubtopic("Drought"))),
                NcertTopic("Clean Drinking Water"),
            )),
            NcertChapter("Abdul in the Garden", listOf(
                NcertTopic("Roots", listOf(NcertSubtopic("Roots in Soil"), NcertSubtopic("Deep Roots"))),
            )),
            NcertChapter("Eating Together", listOf(
                NcertTopic("Community Meals", listOf(NcertSubtopic("Festivals and Food"), NcertSubtopic("Sharing Food"))),
            )),
            NcertChapter("Food and Play", listOf(
                NcertTopic("Nutrition", listOf(NcertSubtopic("Balanced Diet"), NcertSubtopic("Energy from Food"))),
            )),
            NcertChapter("The World in My Home", listOf(
                NcertTopic("Family Roles", listOf(NcertSubtopic("Sharing Work"), NcertSubtopic("Respect for All"))),
            )),
            NcertChapter("Pochampalli", listOf(
                NcertTopic("Weaving", listOf(NcertSubtopic("Handloom"), NcertSubtopic("Types of Cloth"))),
            )),
            NcertChapter("Home and Abroad", listOf(
                NcertTopic("Different Places", listOf(NcertSubtopic("Village vs City"), NcertSubtopic("Different Countries"))),
            )),
            NcertChapter("Spicy Riddles", listOf(
                NcertTopic("Spices", listOf(NcertSubtopic("Common Indian Spices"), NcertSubtopic("Uses of Spices"))),
            )),
            NcertChapter("Defence Officer: Wahida Prism", listOf(
                NcertTopic("Defence Services", listOf(NcertSubtopic("Army, Navy, Air Force"))),
            )),
            NcertChapter("Chuskit Goes to School", listOf(
                NcertTopic("Disability and Access", listOf(NcertSubtopic("Wheelchair Access"), NcertSubtopic("Inclusive Schools"))),
            )),
        )),

        // ════ CLASS 5 ════
        NcertSyllabus("Class 5", "Mathematics", listOf(
            NcertChapter("The Fish Tale", listOf(
                NcertTopic("Numbers and Operations", listOf(NcertSubtopic("Large Numbers"), NcertSubtopic("Multiplication"))),
                NcertTopic("Money Calculations"),
            )),
            NcertChapter("Shapes and Angles", listOf(
                NcertTopic("Angles", listOf(NcertSubtopic("Right Angle, Acute, Obtuse"), NcertSubtopic("Measuring Angles"))),
                NcertTopic("Polygons", listOf(NcertSubtopic("Triangle, Quadrilateral"), NcertSubtopic("Regular and Irregular"))),
            )),
            NcertChapter("How Many Squares?", listOf(
                NcertTopic("Area", listOf(NcertSubtopic("Counting Squares"), NcertSubtopic("Comparing Areas"))),
            )),
            NcertChapter("Parts and Wholes", listOf(
                NcertTopic("Fractions", listOf(NcertSubtopic("Proper and Improper Fractions"), NcertSubtopic("Mixed Numbers"), NcertSubtopic("Addition of Fractions"))),
            )),
            NcertChapter("Does it Look the Same?", listOf(
                NcertTopic("Symmetry", listOf(NcertSubtopic("Line Symmetry"), NcertSubtopic("Rotational Symmetry"))),
            )),
            NcertChapter("Be My Multiple, I'll be Your Factor", listOf(
                NcertTopic("Multiples and Factors", listOf(NcertSubtopic("Common Multiples"), NcertSubtopic("Common Factors"), NcertSubtopic("LCM and HCF"))),
            )),
            NcertChapter("Can You See the Pattern?", listOf(
                NcertTopic("Patterns", listOf(NcertSubtopic("Number Patterns"), NcertSubtopic("Turns and Patterns"))),
            )),
            NcertChapter("Mapping Your Way", listOf(
                NcertTopic("Maps", listOf(NcertSubtopic("Reading Maps"), NcertSubtopic("Scale and Direction"))),
            )),
            NcertChapter("Boxes and Sketches", listOf(
                NcertTopic("3D Shapes", listOf(NcertSubtopic("Cube and Cuboid"), NcertSubtopic("Nets of Solids"))),
                NcertTopic("Drawing 3D Objects"),
            )),
            NcertChapter("Tenths and Hundredths", listOf(
                NcertTopic("Decimals", listOf(NcertSubtopic("Tenths"), NcertSubtopic("Hundredths"), NcertSubtopic("Converting Fractions to Decimals"))),
                NcertTopic("Money and Decimals"),
            )),
            NcertChapter("Area and its Boundary", listOf(
                NcertTopic("Area", listOf(NcertSubtopic("Area of Rectangle"), NcertSubtopic("Area of Square"))),
                NcertTopic("Perimeter", listOf(NcertSubtopic("Perimeter Formula"))),
            )),
            NcertChapter("Smart Charts", listOf(
                NcertTopic("Data Handling", listOf(NcertSubtopic("Bar Graphs"), NcertSubtopic("Line Graphs"), NcertSubtopic("Pie Charts"))),
            )),
            NcertChapter("Ways to Multiply and Divide", listOf(
                NcertTopic("Multiplication", listOf(NcertSubtopic("Two-Digit by Two-Digit"), NcertSubtopic("Three-Digit Multiplication"))),
                NcertTopic("Division", listOf(NcertSubtopic("Long Division"), NcertSubtopic("Division with Remainder"))),
            )),
            NcertChapter("How Big? How Heavy?", listOf(
                NcertTopic("Volume", listOf(NcertSubtopic("Cubic Centimetres"), NcertSubtopic("Measuring Volume"))),
                NcertTopic("Weight", listOf(NcertSubtopic("Grams, Kilograms"), NcertSubtopic("Estimating Weight"))),
            )),
        )),

        NcertSyllabus("Class 5", "English", listOf(
            NcertChapter("Ice Cream Man (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Joy of Ice Cream"))),
            NcertChapter("Wonderful Waste!", listOf(NcertTopic("Story Reading"), NcertTopic("Reducing Waste"))),
            NcertChapter("Bamboo Curry", listOf(NcertTopic("Story Reading"), NcertTopic("Tribal Life"))),
            NcertChapter("Robinson Crusoe (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Adventure"))),
            NcertChapter("The Lazy Frog", listOf(NcertTopic("Story Reading"), NcertTopic("Hard Work vs Laziness"))),
            NcertChapter("Rip Van Winkle", listOf(NcertTopic("Story Reading"), NcertTopic("Classic Tale"))),
            NcertChapter("Topsy-Turvy Land (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Imagination"))),
            NcertChapter("Who Will Be Ningthou?", listOf(NcertTopic("Story Reading"), NcertTopic("Leadership and Justice"))),
            NcertChapter("The Singing Lesson (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Class Discussion", listOf(NcertTopic("Story Reading"), NcertTopic("Communication"))),
            NcertChapter("The Bully (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Anti-Bullying"))),
            NcertChapter("The Shoeshine Boy", listOf(NcertTopic("Story Reading"), NcertTopic("Dignity of Labour"))),
            NcertChapter("Torn Dream (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("The Day the River Spoke", listOf(NcertTopic("Story Reading"), NcertTopic("Nature"))),
            NcertChapter("The Mysterious Picture (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("My Shadow", listOf(NcertTopic("Story Reading"), NcertTopic("Self-Discovery"))),
            NcertChapter("Crying (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Expressing Emotions"))),
            NcertChapter("The Little Bully", listOf(NcertTopic("Story Reading"), NcertTopic("Behaviour"))),
        )),

        NcertSyllabus("Class 5", "Hindi", listOf(
            NcertChapter("राख की रस्सी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("लचीलापन"))),
            NcertChapter("फसलों के त्योहार", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("त्योहार और किसान"))),
            NcertChapter("खिलौनेवाला (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("बचपन और खिलौने"))),
            NcertChapter("नन्हा फनकार", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रतिभा"))),
            NcertChapter("जहाँ चाह वहाँ राह (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("संकल्प"))),
            NcertChapter("चिट्ठियाँ इतिहास बनती हैं", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("इतिहास और पत्र"))),
            NcertChapter("डाकिया का बंडल (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("डाकिया"))),
            NcertChapter("वह दिन भी क्या दिन था", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("स्वतंत्रता संग्राम"))),
            NcertChapter("एक माँ की याद (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("माँ का प्रेम"))),
            NcertChapter("चुनौती हिमालय की", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("साहस और त्याग"))),
            NcertChapter("पहाड़ों की रानी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("प्रकृति"))),
            NcertChapter("गाँव का जीवन", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("ग्रामीण जीवन"))),
            NcertChapter("नौकर (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("सेवा"))),
            NcertChapter("अंतु बर्मा", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("मित्रता"))),
        )),

        NcertSyllabus("Class 5", "EVS", listOf(
            NcertChapter("Super Senses", listOf(
                NcertTopic("Animal Senses", listOf(NcertSubtopic("Smell in Animals"), NcertSubtopic("Hearing in Animals"), NcertSubtopic("Sight in Animals"))),
                NcertTopic("How Animals Use Senses"),
            )),
            NcertChapter("A Snake Charmer's Story", listOf(
                NcertTopic("Snakes", listOf(NcertSubtopic("Types of Snakes"), NcertSubtopic("Venomous and Non-Venomous"))),
                NcertTopic("Snake Charming as a Profession"),
            )),
            NcertChapter("From Tasting to Digesting", listOf(
                NcertTopic("Digestion", listOf(NcertSubtopic("Mouth and Teeth"), NcertSubtopic("Stomach"), NcertSubtopic("Intestines"))),
                NcertTopic("Taste Buds", listOf(NcertSubtopic("Sweet, Sour, Salty, Bitter"))),
            )),
            NcertChapter("Mangoes Round the Year", listOf(
                NcertTopic("Food Preservation", listOf(NcertSubtopic("Drying"), NcertSubtopic("Pickling"), NcertSubtopic("Refrigeration"))),
            )),
            NcertChapter("Seeds and Seeds", listOf(
                NcertTopic("Germination", listOf(NcertSubtopic("Sprouting Seeds"), NcertSubtopic("Conditions for Germination"))),
                NcertTopic("Types of Seeds", listOf(NcertSubtopic("Monocot"), NcertSubtopic("Dicot"))),
            )),
            NcertChapter("Every Drop Counts", listOf(
                NcertTopic("Water Conservation", listOf(NcertSubtopic("Rainwater Harvesting"), NcertSubtopic("Stepwells (Baolis)"))),
            )),
            NcertChapter("Experiments with Water", listOf(
                NcertTopic("Properties of Water", listOf(NcertSubtopic("Sink and Float"), NcertSubtopic("Dissolving in Water"))),
            )),
            NcertChapter("A Treat for Mosquitoes", listOf(
                NcertTopic("Mosquitoes", listOf(NcertSubtopic("Life Cycle of Mosquito"), NcertSubtopic("Diseases Spread by Mosquitoes"))),
                NcertTopic("Prevention", listOf(NcertSubtopic("Cleanliness"), NcertSubtopic("Mosquito Nets"))),
            )),
            NcertChapter("Up You Go!", listOf(
                NcertTopic("Mountains and Climbing", listOf(NcertSubtopic("Equipment"), NcertSubtopic("Safety"))),
            )),
            NcertChapter("Walls Tell Stories", listOf(
                NcertTopic("Historical Monuments", listOf(NcertSubtopic("Forts"), NcertSubtopic("Architecture"))),
            )),
            NcertChapter("Sunita in Space", listOf(
                NcertTopic("Space and Gravity", listOf(NcertSubtopic("Zero Gravity"), NcertSubtopic("Earth from Space"))),
            )),
            NcertChapter("What if it Finishes?", listOf(
                NcertTopic("Fuel", listOf(NcertSubtopic("Petrol and Diesel"), NcertSubtopic("Running Out of Fuel"))),
                NcertTopic("Alternative Energy", listOf(NcertSubtopic("Solar"), NcertSubtopic("Wind"))),
            )),
            NcertChapter("A Shelter So High!", listOf(
                NcertTopic("Houses in Different Climates", listOf(NcertSubtopic("Mountains"), NcertSubtopic("Deserts"), NcertSubtopic("Forests"))),
            )),
            NcertChapter("When the Earth Shook!", listOf(
                NcertTopic("Earthquakes", listOf(NcertSubtopic("Causes"), NcertSubtopic("Safety During Earthquakes"))),
            )),
            NcertChapter("Blow Hot, Blow Cold", listOf(
                NcertTopic("Air", listOf(NcertSubtopic("Hot and Cold Air"), NcertSubtopic("Wind"))),
            )),
            NcertChapter("Who Will Do This Work?", listOf(
                NcertTopic("Dignity of Labour", listOf(NcertSubtopic("All Jobs Are Important"))),
            )),
            NcertChapter("Across the Wall", listOf(
                NcertTopic("Breaking Barriers", listOf(NcertSubtopic("Gender Equality"), NcertSubtopic("Sports and Teamwork"))),
            )),
            NcertChapter("No Place for Us", listOf(
                NcertTopic("Displacement", listOf(NcertSubtopic("Dams and Displacement"), NcertSubtopic("Resettlement"))),
            )),
            NcertChapter("A Seed Tells a Story", listOf(
                NcertTopic("Agriculture", listOf(NcertSubtopic("Traditional Farming"), NcertSubtopic("Modern Farming"))),
            )),
            NcertChapter("Whose Forests?", listOf(
                NcertTopic("Forests", listOf(NcertSubtopic("Forest Life"), NcertSubtopic("Deforestation"), NcertSubtopic("Conservation"))),
            )),
            NcertChapter("Like Father, Like Daughter", listOf(
                NcertTopic("Inheritance", listOf(NcertSubtopic("Traits from Parents"))),
            )),
            NcertChapter("On the Move Again", listOf(
                NcertTopic("Migration", listOf(NcertSubtopic("Reasons for Migration"), NcertSubtopic("Impact on Families"))),
            )),
        )),
    )
}
