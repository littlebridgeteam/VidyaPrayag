package com.littlebridge.enrollplus.feature.ai

import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertChapter
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertSyllabus
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertSubtopic
import com.littlebridge.enrollplus.feature.ai.NcertReferenceService.NcertTopic

object NcertReferenceData3 {

    val DATA: List<NcertSyllabus> = listOf(

        // ════ CLASS 1 ════
        NcertSyllabus("Class 1", "Mathematics", listOf(
            NcertChapter("Shapes and Space", listOf(
                NcertTopic("Recognising Shapes", listOf(NcertSubtopic("Circle, Square, Triangle"), NcertSubtopic("Rectangle"))),
                NcertTopic("Spatial Understanding", listOf(NcertSubtopic("Inside-Outside"), NcertSubtopic("Top-Bottom"), NcertSubtopic("Near-Far"))),
            )),
            NcertChapter("Numbers One to Five", listOf(
                NcertTopic("Counting 1 to 5", listOf(NcertSubtopic("Counting Objects"), NcertSubtopic("Writing Numbers"))),
                NcertTopic("More and Less", listOf(NcertSubtopic("Comparing Quantities"))),
            )),
            NcertChapter("Numbers Six to Ten", listOf(
                NcertTopic("Counting 6 to 10", listOf(NcertSubtopic("Counting Objects"), NcertSubtopic("Writing Numbers"))),
                NcertTopic("Zero", listOf(NcertSubtopic("Concept of Zero"))),
            )),
            NcertChapter("Addition and Subtraction (1 to 10)", listOf(
                NcertTopic("Simple Addition", listOf(NcertSubtopic("Adding with Pictures"), NcertSubtopic("Adding on Fingers"))),
                NcertTopic("Simple Subtraction", listOf(NcertSubtopic("Taking Away"), NcertSubtopic("Subtraction with Pictures"))),
            )),
            NcertChapter("Numbers Eleven to Twenty", listOf(
                NcertTopic("Counting 11 to 20", listOf(NcertSubtopic("Tens and Ones"), NcertSubtopic("Writing Numbers"))),
                NcertTopic("Before, After, Between", listOf(NcertSubtopic("Number Order"))),
            )),
            NcertChapter("Time", listOf(NcertTopic("Days of the Week"), NcertTopic("Morning, Afternoon, Evening, Night"))),
            NcertChapter("Measurement", listOf(NcertTopic("Long and Short"), NcertTopic("Heavy and Light"), NcertTopic("Thick and Thin"))),
            NcertChapter("Data Handling", listOf(NcertTopic("Simple Pictographs"))),
            NcertChapter("Patterns", listOf(NcertTopic("Shape Patterns"), NcertTopic("Number Patterns"))),
            NcertChapter("Numbers Twenty-one to Fifty", listOf(
                NcertTopic("Counting 21 to 50", listOf(NcertSubtopic("Tens and Ones"))),
                NcertTopic("Number Names"),
            )),
            NcertChapter("Money", listOf(NcertTopic("Indian Coins and Notes", listOf(NcertSubtopic("Re 1, Rs 2, Rs 5"))))),
            NcertChapter("How Many? (1 to 100)", listOf(NcertTopic("Counting to 100", listOf(NcertSubtopic("Skip Counting by 10s"))))),
        )),

        NcertSyllabus("Class 1", "English", listOf(
            NcertChapter("A Happy Child (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("New Words"))),
            NcertChapter("Three Little Pigs", listOf(NcertTopic("Story Reading"), NcertTopic("Sequencing Events"))),
            NcertChapter("After a Bath (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Rhyming Words"))),
            NcertChapter("The Bubble, the Straw and the Shoe", listOf(NcertTopic("Story Reading"), NcertTopic("Moral of the Story"))),
            NcertChapter("One Little Kitten", listOf(NcertTopic("Reading"), NcertTopic("Animal Names"))),
            NcertChapter("Lalu and Peelu", listOf(NcertTopic("Story Reading"), NcertTopic("Colours"))),
            NcertChapter("Once I Saw a Little Bird (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Mittu and the Yellow Mango", listOf(NcertTopic("Story Reading"), NcertTopic("Comprehension Questions"))),
            NcertChapter("Merry-Go-Round (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Circle", listOf(NcertTopic("Story Reading"), NcertTopic("Drawing Activity"))),
            NcertChapter("If You Are Happy and You Know It (Poem)", listOf(NcertTopic("Action Song"))),
            NcertChapter("Drawing", listOf(NcertTopic("Reading and Drawing"))),
            NcertChapter("Clouds (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Weather Words"))),
            NcertChapter("Flying Man (Poem)", listOf(NcertTopic("Reading"))),
        )),

        NcertSyllabus("Class 1", "Hindi", listOf(
            NcertChapter("झूला (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("नए शब्द"))),
            NcertChapter("आम की कहानी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रश्नोत्तर"))),
            NcertChapter("आम की टोकरी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("तुकांत शब्द"))),
            NcertChapter("पत्ते ही पत्ते", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रकृति और पत्ते"))),
            NcertChapter("पकौड़ी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("खाना और स्वाद"))),
            NcertChapter("छुक छुक गाड़ी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("यात्रा के शब्द"))),
            NcertChapter("रसोईघर", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("घर के स्थान"))),
            NcertChapter("चूहो! म्याऊँ सो रही है (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("जानवरों की आवाज़"))),
            NcertChapter("आम और खरबूजा", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("फलों के नाम"))),
            NcertChapter("पर्तू आया", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("मौसम"))),
            NcertChapter("पक्की पक्की (कविता)", listOf(NcertTopic("कविता वाचन"))),
            NcertChapter("गेंद-बल्ला", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("खेल"))),
            NcertChapter("बगले चाचा", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("जानवरों की कहानी"))),
            NcertChapter("प्यारा बगुला (कविता)", listOf(NcertTopic("कविता वाचन"))),
        )),

        NcertSyllabus("Class 1", "EVS", listOf(
            NcertChapter("My Body", listOf(
                NcertTopic("Body Parts", listOf(NcertSubtopic("Head, Eyes, Ears, Nose"), NcertSubtopic("Hands and Legs"))),
                NcertTopic("Sense Organs", listOf(NcertSubtopic("Eyes - Seeing"), NcertSubtopic("Ears - Hearing"), NcertSubtopic("Nose - Smelling"), NcertSubtopic("Tongue - Tasting"), NcertSubtopic("Skin - Touching"))),
            )),
            NcertChapter("My Family", listOf(
                NcertTopic("Family Members", listOf(NcertSubtopic("Father, Mother, Siblings"), NcertSubtopic("Grandparents"))),
                NcertTopic("Family Tree"),
            )),
            NcertChapter("My School", listOf(
                NcertTopic("People in School", listOf(NcertSubtopic("Teacher"), NcertSubtopic("Principal"), NcertSubtopic("Helper"))),
                NcertTopic("Classroom Rules"),
            )),
            NcertChapter("Animals", listOf(
                NcertTopic("Pet Animals", listOf(NcertSubtopic("Dog, Cat, Cow"))),
                NcertTopic("Wild Animals", listOf(NcertSubtopic("Lion, Tiger, Elephant"))),
                NcertTopic("Birds", listOf(NcertSubtopic("Parrot, Crow, Sparrow"))),
                NcertTopic("Animal Homes"),
            )),
            NcertChapter("Plants", listOf(
                NcertTopic("Parts of a Plant", listOf(NcertSubtopic("Root"), NcertSubtopic("Stem"), NcertSubtopic("Leaves"), NcertSubtopic("Flower"))),
                NcertTopic("Types of Plants", listOf(NcertSubtopic("Trees"), NcertSubtopic("Shrubs"), NcertSubtopic("Climbers"))),
            )),
            NcertChapter("Food", listOf(
                NcertTopic("Food We Eat", listOf(NcertSubtopic("Fruits and Vegetables"), NcertSubtopic("Cereals and Pulses"))),
                NcertTopic("Healthy Food vs Junk Food"),
            )),
            NcertChapter("Water", listOf(
                NcertTopic("Sources of Water", listOf(NcertSubtopic("Tap, Well, River"))),
                NcertTopic("Uses of Water"),
                NcertTopic("Saving Water"),
            )),
            NcertChapter("Air", listOf(NcertTopic("We Need Air to Breathe"), NcertTopic("Clean and Dirty Air"))),
            NcertChapter("Weather", listOf(
                NcertTopic("Sunny, Rainy, Cloudy", listOf(NcertSubtopic("Seasons"))),
                NcertTopic("Clothes for Different Weather"),
            )),
            NcertChapter("Cleanliness", listOf(
                NcertTopic("Personal Hygiene", listOf(NcertSubtopic("Brushing Teeth"), NcertSubtopic("Washing Hands"), NcertSubtopic("Bathing"))),
                NcertTopic("Keeping Surroundings Clean"),
            )),
        )),

        // ════ CLASS 2 ════
        NcertSyllabus("Class 2", "Mathematics", listOf(
            NcertChapter("What is Long, What is Round?", listOf(
                NcertTopic("Shapes", listOf(NcertSubtopic("Rolling and Sliding"), NcertSubtopic("Edges and Corners"))),
            )),
            NcertChapter("Counting in Groups", listOf(
                NcertTopic("Counting by 2s, 5s, 10s", listOf(NcertSubtopic("Pair Counting"), NcertSubtopic("Group of Ten"))),
                NcertTopic("Estimation"),
            )),
            NcertChapter("How Much Can You Carry?", listOf(
                NcertTopic("Addition", listOf(NcertSubtopic("Adding Two Numbers"), NcertSubtopic("Word Problems"))),
                NcertTopic("Weight", listOf(NcertSubtopic("Heavy and Light"), NcertSubtopic("Comparing Weights"))),
            )),
            NcertChapter("Counting in Tens", listOf(
                NcertTopic("Numbers 10 to 100", listOf(NcertSubtopic("Tens and Ones"), NcertSubtopic("Number Names"))),
            )),
            NcertChapter("Patterns", listOf(
                NcertTopic("Shape Patterns", listOf(NcertSubtopic("Repeating Patterns"), NcertSubtopic("Growing Patterns"))),
                NcertTopic("Number Patterns"),
            )),
            NcertChapter("Footprints", listOf(NcertTopic("Shapes and Traces"), NcertTopic("Symmetry"))),
            NcertChapter("Jugs and Mugs", listOf(
                NcertTopic("Capacity", listOf(NcertSubtopic("More or Less"), NcertSubtopic("Measuring with Cups"))),
            )),
            NcertChapter("Tens and Ones", listOf(
                NcertTopic("Place Value", listOf(NcertSubtopic("Two-Digit Numbers"), NcertSubtopic("Breaking into Tens and Ones"))),
            )),
            NcertChapter("My Funday", listOf(
                NcertTopic("Days of the Week", listOf(NcertSubtopic("Yesterday, Today, Tomorrow"))),
                NcertTopic("Months of the Year"),
            )),
            NcertChapter("Add Our Points", listOf(
                NcertTopic("Addition Practice", listOf(NcertSubtopic("Adding Three Numbers"), NcertSubtopic("Mental Addition"))),
            )),
            NcertChapter("Lines and Lines", listOf(NcertTopic("Straight and Curved Lines"), NcertTopic("Types of Lines"))),
            NcertChapter("Give and Take", listOf(
                NcertTopic("Addition and Subtraction", listOf(NcertSubtopic("Two-Digit Addition"), NcertSubtopic("Two-Digit Subtraction"), NcertSubtopic("Word Problems"))),
            )),
            NcertChapter("The Longest Step", listOf(
                NcertTopic("Length", listOf(NcertSubtopic("Measuring with Handspan"), NcertSubtopic("Measuring with Ruler"))),
            )),
            NcertChapter("Birds Come, Birds Go", listOf(
                NcertTopic("Addition and Subtraction Stories"),
                NcertTopic("Data Handling", listOf(NcertSubtopic("Simple Tables"))),
            )),
            NcertChapter("How Many Ponytails?", listOf(
                NcertTopic("Counting and Estimation", listOf(NcertSubtopic("Numbers up to 100"))),
                NcertTopic("Multiplication Concept", listOf(NcertSubtopic("Repeated Addition"))),
            )),
        )),

        NcertSyllabus("Class 2", "English", listOf(
            NcertChapter("First Day at School (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("School Words"))),
            NcertChapter("Haldis Adventure", listOf(NcertTopic("Story Reading"), NcertTopic("Comprehension"))),
            NcertChapter("The Wind and the Sun", listOf(NcertTopic("Story Reading"), NcertTopic("Moral Values"))),
            NcertChapter("Rain (Poem)", listOf(NcertTopic("Reading and Recitation"), NcertTopic("Weather Words"))),
            NcertChapter("Storm in the Garden", listOf(NcertTopic("Story Reading"), NcertTopic("Nature and Animals"))),
            NcertChapter("Funny Bunny", listOf(NcertTopic("Story Reading"), NcertTopic("Humour"))),
            NcertChapter("The Panchatantra: The Lion and the Mouse", listOf(NcertTopic("Story Reading"), NcertTopic("Moral of the Story"))),
            NcertChapter("Mr. Nobody (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("Curlylocks and the Three Bears", listOf(NcertTopic("Story Reading"), NcertTopic("Fairy Tales"))),
            NcertChapter("The Grasshopper and the Ants", listOf(NcertTopic("Story Reading"), NcertTopic("Moral Values"))),
            NcertChapter("The Tailor and His Friend", listOf(NcertTopic("Story Reading"), NcertTopic("Friendship"))),
            NcertChapter("Stray Dog (Poem)", listOf(NcertTopic("Reading and Recitation"))),
            NcertChapter("The Smart Monkey", listOf(NcertTopic("Story Reading"), NcertTopic("Animal Stories"))),
            NcertChapter("The Praying Mantis", listOf(NcertTopic("Reading"), NcertTopic("Insects"))),
        )),

        NcertSyllabus("Class 2", "Hindi", listOf(
            NcertChapter("ऊँट चला (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("जानवरों के बारे में"))),
            NcertChapter("भालू आया", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("प्रश्नोत्तर"))),
            NcertChapter("म्याऊँ, म्याऊँ (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("तुकांत"))),
            NcertChapter("अधिक बलवान कौन?", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("नैतिक शिक्षा"))),
            NcertChapter("दोहा", listOf(NcertTopic("कविता वाचन"), NcertTopic("दोहे का अर्थ"))),
            NcertChapter("कौन बनाएगा पकौड़ी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("खाना बनाना"))),
            NcertChapter("कितने पैर? (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("जानवरों के पैर"))),
            NcertChapter("सबसे सुंदर लड़की", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("सुंदरता"))),
            NcertChapter("नखट (कविता)", listOf(NcertTopic("कविता वाचन"))),
            NcertChapter("चुभन गुब्बारा", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("हँसी की कहानी"))),
            NcertChapter("टीम बढ़ी (कविता)", listOf(NcertTopic("कविता वाचन"), NcertTopic("पशु-पक्षी"))),
            NcertChapter("बंदर और गिलहरी", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("पंचतंत्र की कहानी"))),
            NcertChapter("बिल्ली बोली (कविता)", listOf(NcertTopic("कविता वाचन"))),
            NcertChapter("घर हमारा", listOf(NcertTopic("कहानी पढ़ना"), NcertTopic("घर के प्रकार"))),
        )),

        NcertSyllabus("Class 2", "EVS", listOf(
            NcertChapter("Insects", listOf(
                NcertTopic("Common Insects", listOf(NcertSubtopic("Butterfly, Ant, Honeybee"), NcertSubtopic("Housefly, Mosquito"))),
                NcertTopic("Useful and Harmful Insects"),
            )),
            NcertChapter("The World of Animals", listOf(
                NcertTopic("Animal Homes", listOf(NcertSubtopic("Nest, Kennel, Stable"), NcertSubtopic("Den, Burrow"))),
                NcertTopic("What Animals Eat", listOf(NcertSubtopic("Herbivores"), NcertSubtopic("Carnivores"), NcertSubtopic("Omnivores"))),
            )),
            NcertChapter("Plants Around Us", listOf(
                NcertTopic("Types of Plants", listOf(NcertSubtopic("Big Trees"), NcertSubtopic("Small Plants"), NcertSubtopic("Climbers and Creepers"))),
                NcertTopic("Flowers We Know"),
            )),
            NcertChapter("Water", listOf(
                NcertTopic("Uses of Water", listOf(NcertSubtopic("Drinking"), NcertSubtopic("Cooking"), NcertSubtopic("Cleaning"), NcertSubtopic("Gardening"))),
                NcertTopic("Saving Water"),
            )),
            NcertChapter("Our Neighbourhood", listOf(
                NcertTopic("Places in Neighbourhood", listOf(NcertSubtopic("Post Office"), NcertSubtopic("Hospital"), NcertSubtopic("Market"), NcertSubtopic("Park"))),
                NcertTopic("Helpers in Neighbourhood"),
            )),
            NcertChapter("Means of Transport", listOf(
                NcertTopic("Land Transport", listOf(NcertSubtopic("Bus, Car, Train"), NcertSubtopic("Bicycle"))),
                NcertTopic("Water Transport", listOf(NcertSubtopic("Boat, Ship"))),
                NcertTopic("Air Transport", listOf(NcertSubtopic("Aeroplane, Helicopter"))),
            )),
            NcertChapter("Means of Communication", listOf(
                NcertTopic("Ways to Communicate", listOf(NcertSubtopic("Letter and Postcard"), NcertSubtopic("Telephone"), NcertSubtopic("Mobile Phone"))),
            )),
            NcertChapter("Our Festivals", listOf(
                NcertTopic("National Festivals", listOf(NcertSubtopic("Independence Day"), NcertSubtopic("Republic Day"), NcertSubtopic("Gandhi Jayanti"))),
                NcertTopic("Religious Festivals", listOf(NcertSubtopic("Diwali, Holi"), NcertSubtopic("Eid, Christmas"))),
            )),
            NcertChapter("Our Earth", listOf(NcertTopic("The Earth is Our Home"), NcertTopic("Natural Things vs Man-Made Things"))),
            NcertChapter("Good Habits", listOf(
                NcertTopic("Healthy Habits", listOf(NcertSubtopic("Early to Bed, Early to Rise"), NcertSubtopic("Washing Hands Before Meals"), NcertSubtopic("Brushing Teeth Twice"))),
                NcertTopic("Good Manners", listOf(NcertSubtopic("Saying Please and Thank You"), NcertSubtopic("Respecting Elders"))),
            )),
        )),
    )
}
