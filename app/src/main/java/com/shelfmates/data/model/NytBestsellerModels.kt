package com.shelfmates.data.model

/**
 * Data model representing a book featured on The New York Times Best Sellers list.
 */
data class NytBestsellerBook(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val rank: Int,
    val listName: String,
    val weeksOnList: Int,
    val coverUrl: String,
    val isbn: String,
    val nytUrl: String = "https://www.nytimes.com/books/best-sellers/",
    val category: String = "Fiction",
    val publisher: String = "",
    val quoteOrPraise: String = "Selected by The New York Times Book Review"
) {
    fun toGoogleBookVolumeItem(): GoogleBookVolumeItem {
        return GoogleBookVolumeItem(
            id = id,
            volumeInfo = VolumeInfo(
                title = title,
                authors = listOf(author),
                description = description,
                publisher = publisher,
                categories = listOf(category),
                industryIdentifiers = listOf(IndustryIdentifier(type = "ISBN_13", identifier = isbn)),
                imageLinks = ImageLinks(thumbnail = coverUrl, smallThumbnail = coverUrl, extraLarge = coverUrl)
            )
        )
    }
}

/**
 * Curated The New York Times Bestsellers for the rotating front-page showcase.
 */
object NytBestsellerData {
    const val NYT_BESTSELLERS_URL = "https://www.nytimes.com/books/best-sellers/"

    val rotatingFrontPageBooks = listOf(
        NytBestsellerBook(
            id = "nyt_james_everett",
            title = "James",
            author = "Percival Everett",
            description = "A brilliant, action-packed reimagining of Adventures of Huckleberry Finn, told from the perspective of the enslaved Jim with fierce intelligence, humor, and devastating resonance.",
            rank = 1,
            listName = "Combined Print & E-Book Fiction",
            weeksOnList = 26,
            coverUrl = "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&auto=format&fit=crop&q=80",
            isbn = "9780385550369",
            category = "Literary Fiction",
            publisher = "Doubleday",
            quoteOrPraise = "“A masterpiece that will become an American classic.” — The New York Times"
        ),
        NytBestsellerBook(
            id = "nyt_the_women_hannah",
            title = "The Women",
            author = "Kristin Hannah",
            description = "An intimate, epic portrait of coming of age in the Vietnam War and the brave army nurses who served their country, only to return to a deeply divided America.",
            rank = 1,
            listName = "Hardcover Fiction",
            weeksOnList = 34,
            coverUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop&q=80",
            isbn = "9781250178633",
            category = "Historical Fiction",
            publisher = "St. Martin's Press",
            quoteOrPraise = "“Heart-stoppingly emotional and deeply researched.” — The New York Times Book Review"
        ),
        NytBestsellerBook(
            id = "nyt_fourth_wing_yarros",
            title = "Fourth Wing",
            author = "Rebecca Yarros",
            description = "Twenty-year-old Violet Sorrengail was supposed to enter the quiet Scribe Quadrant, but is thrust into the deadly Basgiath War College where dragon riders fight to survive.",
            rank = 2,
            listName = "Hardcover Fiction",
            weeksOnList = 58,
            coverUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=600&auto=format&fit=crop&q=80",
            isbn = "9781649374042",
            category = "Fantasy & Romance",
            publisher = "Red Tower Books",
            quoteOrPraise = "“A gripping fantasy phenomenon that captivated millions of readers.” — The New York Times"
        ),
        NytBestsellerBook(
            id = "nyt_tomorrow_zevin",
            title = "Tomorrow, and Tomorrow, and Tomorrow",
            author = "Gabrielle Zevin",
            description = "Two childhood friends reunited in college become legendary video game designers. A multi-decade exploration of creativity, love, grief, and intellectual partnership.",
            rank = 3,
            listName = "Paperback Trade Fiction",
            weeksOnList = 72,
            coverUrl = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600&auto=format&fit=crop&q=80",
            isbn = "9780593321201",
            category = "Contemporary Fiction",
            publisher = "Knopf",
            quoteOrPraise = "“Delightful and absorbing, a true literary love letter.” — The New York Times"
        ),
        NytBestsellerBook(
            id = "nyt_yellowface_kuang",
            title = "Yellowface",
            author = "R.F. Kuang",
            description = "A propulsive satire that takes down cultural appropriation, publishing industry greed, and viral social media hysteria through the gripping lens of a stolen manuscript.",
            rank = 4,
            listName = "Hardcover Fiction",
            weeksOnList = 42,
            coverUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop&q=80",
            isbn = "9780063250833",
            category = "Satirical Thriller",
            publisher = "William Morrow",
            quoteOrPraise = "“A wicked, razor-sharp literary thrill ride.” — The New York Times"
        ),
        NytBestsellerBook(
            id = "nyt_demon_copperhead",
            title = "Demon Copperhead",
            author = "Barbara Kingsolver",
            description = "Winner of the Pulitzer Prize for Fiction. Set in the mountains of southern Appalachia, a fierce boy navigates the perils of foster care, child labor, and addiction.",
            rank = 2,
            listName = "Paperback Trade Fiction",
            weeksOnList = 80,
            coverUrl = "https://images.unsplash.com/photo-1495640388908-05fa85288e61?w=600&auto=format&fit=crop&q=80",
            isbn = "9780063251922",
            category = "Literary Fiction",
            publisher = "Harper",
            quoteOrPraise = "“An unforgettable epic of modern America.” — The New York Times"
        )
    )
}

/**
 * Intelligent book search suggestions helper for instant autocomplete and recommendations.
 */
object BookSearchSuggestions {
    val trendingTitles = listOf(
        "James by Percival Everett",
        "The Women by Kristin Hannah",
        "Fourth Wing by Rebecca Yarros",
        "Iron Flame by Rebecca Yarros",
        "Tomorrow, and Tomorrow, and Tomorrow",
        "Yellowface by R.F. Kuang",
        "Demon Copperhead by Barbara Kingsolver",
        "Lessons in Chemistry by Bonnie Garmus",
        "Project Hail Mary by Andy Weir",
        "The Midnight Library by Matt Haig",
        "All the Colors of the Dark by Chris Whitaker",
        "Funny Story by Emily Henry",
        "Dune by Frank Herbert",
        "A Court of Thorns and Roses",
        "House of Flame and Shadow",
        "The Heaven & Earth Grocery Store",
        "Intermezzo by Sally Rooney",
        "First Lie Wins by Ashley Elston",
        "Klara and the Sun by Kazuo Ishiguro",
        "Babel by R.F. Kuang"
    )

    val popularAuthors = listOf(
        "Percival Everett",
        "Kristin Hannah",
        "Rebecca Yarros",
        "Emily Henry",
        "Barbara Kingsolver",
        "Gabrielle Zevin",
        "R.F. Kuang",
        "Brandon Sanderson",
        "Stephen King",
        "Sarah J. Maas",
        "Sally Rooney",
        "Andy Weir",
        "Matt Haig",
        "Colleen Hoover",
        "Neil Gaiman"
    )

    val curatedTopics = listOf(
        "NYT Bestsellers",
        "Book Club Favorites",
        "Sci-Fi Epics",
        "Fantasy & Lore",
        "Historical Fiction",
        "Psychological Thriller",
        "Literary Award Winners",
        "Contemporary Romance",
        "Memoirs"
    )

    fun getSuggestions(query: String, maxCount: Int = 6): List<String> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return trendingTitles.take(maxCount)
        }

        val matchingTitles = trendingTitles.filter { it.contains(trimmed, ignoreCase = true) }
        val matchingAuthors = popularAuthors
            .filter { it.contains(trimmed, ignoreCase = true) }
            .map { "Author: $it" }
        val matchingTopics = curatedTopics
            .filter { it.contains(trimmed, ignoreCase = true) }
            .map { "Topic: $it" }

        return (matchingTitles + matchingAuthors + matchingTopics)
            .distinct()
            .take(maxCount)
    }
}
