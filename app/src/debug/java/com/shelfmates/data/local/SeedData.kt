package com.shelfmates.data.local

import com.shelfmates.data.model.ApplicationStatus
import com.shelfmates.data.model.ArcStatus
import com.shelfmates.data.model.BroadcastType
import com.shelfmates.data.model.NotificationType
import com.shelfmates.data.model.ReviewStatus
import com.shelfmates.data.model.UserRole

object SeedData {

    suspend fun populateDatabase(dao: ShelfmatesDao) {
        // Users
        val users = listOf(
            UserEntity(
                id = "user_ray",
                role = UserRole.AUTHOR,
                displayName = "Ray K. Vance",
                email = "ray.author@shelfmates.app",
                phone = "+1 555-0192",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                bio = "Indie Fantasy & Sci-Fi Author. Author of 'The Last Chronomancer' trilogy. Atomic Shelf Partner Author.",
                genresCsv = "Fantasy,Sci-Fi,LitRPG,Thriller",
                amazonAuthorUrl = "https://amazon.com/author/rayvance",
                asClientId = "AS-CLI-8492",
                isAuthorPro = true,
                followersCount = 428,
                followingCount = 34,
                booksCount = 3
            ),
            UserEntity(
                id = "user_priya",
                role = UserRole.READER,
                displayName = "Priya Sharma",
                email = "priya.reads@gmail.com",
                phone = "+1 555-0144",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                bio = "Avid ARC reviewer (4-6 books/month). Top 500 Reviewer on Goodreads. Passionate about indie sci-fi and speculative fiction.",
                genresCsv = "Sci-Fi,Fantasy,Romance,Mystery",
                amazonAuthorUrl = "",
                asClientId = "",
                isAuthorPro = false,
                followersCount = 89,
                followingCount = 112,
                booksCount = 48
            ),
            UserEntity(
                id = "user_jamie",
                role = UserRole.READER,
                displayName = "Jamie Miller",
                email = "jamie.m@outlook.com",
                phone = "+1 555-0188",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                bio = "Weekend book club hopper, fantasy reader, audio room talker. Always searching for hidden indie gems.",
                genresCsv = "Fantasy,LitRPG,YA,Mystery",
                amazonAuthorUrl = "",
                asClientId = "",
                isAuthorPro = false,
                followersCount = 24,
                followingCount = 67,
                booksCount = 22
            ),
            UserEntity(
                id = "user_elena",
                role = UserRole.AUTHOR,
                displayName = "Elena Vance",
                email = "elena@vancebooks.io",
                phone = "+1 555-0177",
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
                bio = "Hugo-nominated indie space opera novelist. Writing about rogue AI and galactic syndicates.",
                genresCsv = "Sci-Fi,Cyberpunk,Space Opera",
                amazonAuthorUrl = "https://amazon.com/author/elenavance",
                asClientId = "AS-CLI-7120",
                isAuthorPro = true,
                followersCount = 1240,
                followingCount = 45,
                booksCount = 5
            ),
            UserEntity(
                id = "user_marcus",
                role = UserRole.AUTHOR,
                displayName = "Marcus Drake",
                email = "marcus@drakefantasy.com",
                phone = "+1 555-0122",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                bio = "Dark Fantasy & Grimdark author. Blood, steel, and forgotten gods. Running active ARC campaigns.",
                genresCsv = "Dark Fantasy,Horror,LitRPG",
                amazonAuthorUrl = "https://amazon.com/author/marcusdrake",
                asClientId = "AS-CLI-3904",
                isAuthorPro = true,
                followersCount = 890,
                followingCount = 28,
                booksCount = 4
            ),
            UserEntity(
                id = "user_club_member",
                role = UserRole.BOOK_CLUB_MEMBER,
                displayName = "Chloe Bennett",
                email = "chloe.clubs@shelfmates.app",
                phone = "+1 555-0166",
                avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1",
                bio = "Book club moderator & chapter read-along organizer. Hosts weekly live voice debates on indie fiction.",
                genresCsv = "Sci-Fi,Fantasy,Mystery,Historical",
                amazonAuthorUrl = "",
                asClientId = "",
                isAuthorPro = false,
                followersCount = 156,
                followingCount = 92,
                booksCount = 35
            ),
            UserEntity(
                id = "user_admin",
                role = UserRole.ADMIN,
                displayName = "Sarah Jenkins",
                email = "sarah.ops@atomicshelf.com",
                phone = "+1 555-0100",
                avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2",
                bio = "Atomic Shelf Platform Lead & Campaign Portfolio Manager. Overseeing ARC campaigns, review metrics & author growth.",
                genresCsv = "All Genres",
                amazonAuthorUrl = "",
                asClientId = "AS-INTERNAL-HQ",
                isAuthorPro = true,
                followersCount = 2400,
                followingCount = 15,
                booksCount = 0
            )
        )
        dao.insertUsers(users)

        // Public Clubs
        val publicClubs = listOf(
            PublicClubEntity(
                id = "club_scifi_explorers",
                name = "Indie Sci-Fi Explorers",
                genre = "Sci-Fi",
                description = "Exploring hard sci-fi, cyberpunk, and space operas from independent and self-published authors worldwide.",
                coverUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa",
                currentBookId = "book_nebula",
                currentBookTitle = "Nebula's Edge: The Void Protocol",
                currentBookAuthor = "Elena Vance",
                currentBookCover = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                currentBookDescription = "When Captain Maya discovers an ancient precursor signal near Kepler-186f, the crew must choose between corporate allegiance and human survival.",
                currentBookAmazonUrl = "https://www.amazon.com/dp/B08ELENA02",
                currentBookGoodreadsUrl = "https://www.goodreads.com/book/show/nebula-edge",
                adminUserId = "user_elena",
                adminName = "Elena Vance",
                memberCount = 142,
                isJoined = true,
                isVoiceRoomActive = true,
                activeVoiceListeners = 14,
                announcement = "Live audio discussion this Thursday at 7 PM EST! We are diving deep into Chapter 18."
            ),
            PublicClubEntity(
                id = "club_epic_fantasy",
                name = "Epic Fantasy & Worldbuilding",
                genre = "Fantasy",
                description = "For readers who crave vast magic systems, rich lore, map reveals, and grand quests. Weekly chapter read-alongs!",
                coverUrl = "https://images.unsplash.com/photo-1514539079130-25950c84af65",
                currentBookId = "book_chrono",
                currentBookTitle = "The Last Chronomancer",
                currentBookAuthor = "Ray K. Vance",
                currentBookCover = "https://images.unsplash.com/photo-1532012164546-f432f2e3edd4",
                currentBookDescription = "Time is fracturing. A disgraced archivist discovers the gears of reality can be rewound, but every minute stolen extracts a mortal toll.",
                currentBookAmazonUrl = "https://www.amazon.com/dp/B09XRAY101",
                currentBookGoodreadsUrl = "https://www.goodreads.com/book/show/last-chronomancer",
                adminUserId = "user_ray",
                adminName = "Ray K. Vance",
                memberCount = 286,
                isJoined = true,
                isVoiceRoomActive = false,
                activeVoiceListeners = 0,
                announcement = "ARC readers please submit your reviews on Amazon before launch date next Tuesday!"
            ),
            PublicClubEntity(
                id = "club_cozy_mystery",
                name = "Cozy Mystery & Tea",
                genre = "Mystery",
                description = "Small towns, lovable detectives, feline sidekicks, and zero gore. Bring your favorite mug of tea.",
                coverUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c",
                currentBookId = "book_lavender",
                currentBookTitle = "A Lavender Scented Murder",
                currentBookAuthor = "Clara Sterling",
                currentBookCover = "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6",
                currentBookDescription = "When the village baker is found poisoned during the annual Lavender Blossom Festival, retired herbalist Penelope must clear her nephew's name.",
                currentBookAmazonUrl = "https://www.amazon.com/dp/B07CLARA88",
                currentBookGoodreadsUrl = "https://www.goodreads.com/book/show/lavender-murder",
                adminUserId = "user_priya",
                adminName = "Priya Sharma",
                memberCount = 98,
                isJoined = false,
                isVoiceRoomActive = false,
                activeVoiceListeners = 0,
                announcement = "Voting for next month's read is now open in the threads!"
            ),
            PublicClubEntity(
                id = "club_litrpg_hub",
                name = "LitRPG & Progression Hub",
                genre = "LitRPG",
                description = "Stats, dungeon crawls, cultivation realms, and numbers going up. Discussing Royal Road crossovers and Kindle Unlimited hits.",
                coverUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420",
                currentBookId = "book_dungeon",
                currentBookTitle = "Dungeon Architect: Level 1",
                currentBookAuthor = "Marcus Drake",
                currentBookCover = "https://images.unsplash.com/photo-1550745165-9bc0b252726f",
                currentBookDescription = "Reincarnated not as a hero, but as the dungeon core itself. Manage monster waves, trap triggers, and loot tables to survive.",
                currentBookAmazonUrl = "https://www.amazon.com/dp/B07DRAKE33",
                currentBookGoodreadsUrl = "https://www.goodreads.com/book/show/dungeon-architect",
                adminUserId = "user_marcus",
                adminName = "Marcus Drake",
                memberCount = 310,
                isJoined = false,
                isVoiceRoomActive = true,
                activeVoiceListeners = 28,
                announcement = "Voice room is live right now! Author Q&A on magic scaling."
            ),
            PublicClubEntity(
                id = "club_dark_romance",
                name = "Dark Romance & Morally Grey",
                genre = "Romance",
                description = "For readers who love enemies-to-lovers, brooding anti-heroes, gothic mansions, and delicious tension.",
                coverUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7",
                currentBookId = "book_thorn",
                currentBookTitle = "Whispers in the Thorn",
                currentBookAuthor = "Seraphina Black",
                currentBookCover = "https://images.unsplash.com/photo-1474932430478-367dbb6832c1",
                currentBookDescription = "An arranged marriage between rival criminal dynasties sparks a deadly game of obsession, secrets, and betrayed vows.",
                currentBookAmazonUrl = "https://www.amazon.com/dp/B08SERA99",
                currentBookGoodreadsUrl = "https://www.goodreads.com/book/show/whispers-thorn",
                adminUserId = "user_ray",
                adminName = "Ray K. Vance",
                memberCount = 185,
                isJoined = false,
                isVoiceRoomActive = false,
                activeVoiceListeners = 0,
                announcement = "Spoiler discussions for Part 3 are now unlocked!"
            )
        )
        dao.insertPublicClubs(publicClubs)

        // ARC Clubs
        val arcClubs = listOf(
            ArcClubEntity(
                id = "arc_chrono",
                bookId = "book_chrono",
                bookTitle = "The Last Chronomancer",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                coverUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3edd4",
                genre = "Fantasy",
                blurb = "Advance Review Copy. Full unpublished manuscript. Join the official launch campaign! Read before launch date and leave an honest review on Amazon and Goodreads.",
                format = "EPUB & PDF (2.8 MB)",
                slotLimit = 50,
                slotsFilled = 38,
                deadlineDate = "Sep 15, 2026",
                daysRemaining = 12,
                fileUrl = "https://shelfmates-storage.s3.amazonaws.com/arcs/the_last_chronomancer_arc.epub",
                fileSizeMb = 2.8,
                asin = "B09XRAY101",
                status = ArcStatus.OPEN,
                minimumReviewsRequired = 2,
                isApplied = true,
                isApproved = true,
                hasDownloaded = true,
                isReviewSubmitted = true
            ),
            ArcClubEntity(
                id = "arc_singularity",
                bookId = "book_singularity",
                bookTitle = "Singularity's Child: A Cyberpunk Epic",
                authorUserId = "user_elena",
                authorName = "Elena Vance",
                coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                genre = "Sci-Fi",
                blurb = "Exclusive pre-release ARC for Elena Vance's highly anticipated space opera thriller. Limited slots for dedicated science fiction reviewers.",
                format = "EPUB (3.4 MB)",
                slotLimit = 40,
                slotsFilled = 40,
                deadlineDate = "Sep 5, 2026",
                daysRemaining = 4,
                fileUrl = "https://shelfmates-storage.s3.amazonaws.com/arcs/singularitys_child.epub",
                fileSizeMb = 3.4,
                asin = "B08ELENA02",
                status = ArcStatus.OPEN,
                minimumReviewsRequired = 1,
                isApplied = true,
                isApproved = true,
                hasDownloaded = true,
                isReviewSubmitted = false
            ),
            ArcClubEntity(
                id = "arc_bone_alchemist",
                bookId = "book_bone_alchemist",
                bookTitle = "The Bone Alchemist: Book 1",
                authorUserId = "user_marcus",
                authorName = "Marcus Drake",
                coverUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420",
                genre = "Dark Fantasy",
                blurb = "In a city built on the ribcage of a fallen titan, alchemy is performed with bone dust. Seeking fantasy reviewers for early honest feedback.",
                format = "EPUB & MOBI (4.1 MB)",
                slotLimit = 75,
                slotsFilled = 24,
                deadlineDate = "Sep 22, 2026",
                daysRemaining = 18,
                fileUrl = "https://shelfmates-storage.s3.amazonaws.com/arcs/bone_alchemist_advance.epub",
                fileSizeMb = 4.1,
                asin = "B07DRAKE33",
                status = ArcStatus.OPEN,
                minimumReviewsRequired = 0,
                isApplied = false,
                isApproved = false,
                hasDownloaded = false,
                isReviewSubmitted = false
            ),
            ArcClubEntity(
                id = "arc_hearts_orbit",
                bookId = "book_hearts_orbit",
                bookTitle = "Hearts in Orbit: A Stellar Romance",
                authorUserId = "user_elena",
                authorName = "Elena Vance",
                coverUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7",
                genre = "Romance",
                blurb = "Two rival engineers trapped in an orbital relay station have 48 hours to fix the thrusters or drift into deep space forever.",
                format = "EPUB & PDF (1.9 MB)",
                slotLimit = 30,
                slotsFilled = 28,
                deadlineDate = "Sep 3, 2026",
                daysRemaining = 2,
                fileUrl = "https://shelfmates-storage.s3.amazonaws.com/arcs/hearts_orbit.epub",
                fileSizeMb = 1.9,
                asin = "B05JENK44",
                status = ArcStatus.OPEN,
                minimumReviewsRequired = 1,
                isApplied = false,
                isApproved = false,
                hasDownloaded = false,
                isReviewSubmitted = false
            )
        )
        dao.insertArcClubs(arcClubs)

        // Applications
        val applications = listOf(
            ArcApplicationEntity(
                id = "app_priya_chrono",
                arcClubId = "arc_chrono",
                bookTitle = "The Last Chronomancer",
                readerUserId = "user_priya",
                readerName = "Priya Sharma",
                readerAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                message = "I read Ray's previous books and love temporal mechanics in fantasy. I guarantee an in-depth review on Amazon and Goodreads within 5 days of receiving.",
                goodreadsUrl = "https://goodreads.com/priyasharma_reads",
                pastReviewsCount = 42,
                status = ApplicationStatus.APPROVED,
                appliedAt = "2 days ago"
            ),
            ArcApplicationEntity(
                id = "app_sam_chrono",
                arcClubId = "arc_chrono",
                bookTitle = "The Last Chronomancer",
                readerUserId = "user_jamie",
                readerName = "Jamie Miller",
                readerAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                message = "Huge fan of fantasy novels with intricate magic rules. Excited to read and leave early launch feedback!",
                goodreadsUrl = "https://goodreads.com/jamiemiller_books",
                pastReviewsCount = 18,
                status = ApplicationStatus.PENDING,
                appliedAt = "4 hours ago"
            ),
            ArcApplicationEntity(
                id = "app_jordan_chrono",
                arcClubId = "arc_chrono",
                bookTitle = "The Last Chronomancer",
                readerUserId = "user_jordan",
                readerName = "Jordan Hayes",
                readerAvatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6",
                message = "Top reviewer on BookTok & Amazon. Would love to feature this in my upcoming 'Best Indie Fantasy 2026' video roundup.",
                goodreadsUrl = "https://goodreads.com/jordanhayes",
                pastReviewsCount = 65,
                status = ApplicationStatus.PENDING,
                appliedAt = "1 hour ago"
            )
        )
        dao.insertArcApplications(applications)

        // Reviews
        val reviews = listOf(
            ArcReviewEntity(
                id = "rev_priya_chrono",
                arcClubId = "arc_chrono",
                bookTitle = "The Last Chronomancer",
                authorUserId = "user_ray",
                readerUserId = "user_priya",
                readerName = "Priya Sharma",
                readerAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                rating = 5,
                reviewText = "An absolute triumph of indie fantasy! The temporal decay magic system is one of the freshest concepts I've read in years. The character growth of Jonathan from an isolated archivist to a reluctant timeline guardian is compelling and deeply emotional. Highly recommended for fans of Brandon Sanderson and Robin Hobb.",
                amazonPosted = true,
                goodreadsPosted = true,
                status = ReviewStatus.REVIEW_SUBMITTED,
                submittedAt = "Yesterday"
            ),
            ArcReviewEntity(
                id = "rev_liam_chrono",
                arcClubId = "arc_chrono",
                bookTitle = "The Last Chronomancer",
                authorUserId = "user_ray",
                readerUserId = "user_liam",
                readerName = "Liam O'Connor",
                readerAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                rating = 4,
                reviewText = "Fantastic pacing and worldbuilding. The stakes in Act 3 were nail-biting. Knocked off one star only because I wanted more time exploring the Sunken Clocktower district. Pre-ordered my physical copy!",
                amazonPosted = true,
                goodreadsPosted = false,
                status = ReviewStatus.REVIEW_SUBMITTED,
                submittedAt = "3 days ago"
            )
        )
        dao.insertArcReviews(reviews)

        // Club Threads
        val threads = listOf(
            ClubThreadEntity(
                id = "thread_1",
                clubId = "club_scifi_explorers",
                clubType = "PUBLIC",
                category = "Chapter Discussion",
                title = "Chapters 1-5 Discussion: That opening sequence!",
                body = "What did everyone think of Captain Maya's initial encounter with the anomaly? Do you think the beacon was sent intentionally or as a distress signal?",
                authorUserId = "user_elena",
                authorName = "Elena Vance",
                authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
                replyCount = 14,
                likesCount = 28,
                isPinned = true,
                createdAt = "Yesterday"
            ),
            ClubThreadEntity(
                id = "thread_2",
                clubId = "club_scifi_explorers",
                clubType = "PUBLIC",
                category = "Theories",
                title = "The Precursor Language Theory (Spoilers up to Ch 12)",
                body = "I noticed the geometric patterns in the ship interface correlate directly with prime numbers. Here is my breakdown of how the translation engine works...",
                authorUserId = "user_priya",
                authorName = "Priya Sharma",
                authorAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                replyCount = 8,
                likesCount = 19,
                isPinned = false,
                createdAt = "2 days ago"
            ),
            ClubThreadEntity(
                id = "thread_3",
                clubId = "club_epic_fantasy",
                clubType = "PUBLIC",
                category = "Worldbuilding",
                title = "Magic System Breakdown: The Three Hourglasses",
                body = "Let's discuss how the sand of each Hourglass consumes different memories. Does anyone else think the gold hourglass is cursed from the First Era?",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                replyCount = 21,
                likesCount = 45,
                isPinned = true,
                createdAt = "3 days ago"
            )
        )
        dao.insertThreads(threads)

        // Thread Replies
        val replies = listOf(
            ThreadReplyEntity(
                id = "rep_1",
                threadId = "thread_1",
                userId = "user_priya",
                userName = "Priya Sharma",
                userAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                body = "I loved how grounded the ship physics felt during the decompression scene! It set the atmosphere immediately.",
                createdAt = "Yesterday at 4:15 PM"
            ),
            ThreadReplyEntity(
                id = "rep_2",
                threadId = "thread_1",
                userId = "user_jamie",
                userName = "Jamie Miller",
                userAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
                body = "Definitely intentional! The frequency modulation matches the pulsar telemetry mentioned in the prologue.",
                createdAt = "Yesterday at 5:30 PM"
            ),
            ThreadReplyEntity(
                id = "rep_3",
                threadId = "thread_3",
                userId = "user_priya",
                userName = "Priya Sharma",
                userAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9",
                body = "The memory sacrifice mechanic gave me chills. Losing your childhood memories just to cast a 3-second rewind is brutal.",
                createdAt = "2 days ago"
            )
        )
        dao.insertThreadReplies(replies)

        // Broadcasts
        val broadcasts = listOf(
            BroadcastEntity(
                id = "bc_1",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
                title = "Cover Reveal & Launch Date!",
                message = "The official illustrated cover for The Last Chronomancer is here! ARC reviewers, make sure to check your emails and app for download keys.",
                type = BroadcastType.COVER_REVEAL,
                actionUrl = "https://amazon.com/dp/B09XRAY101",
                sentAt = "3 hours ago"
            ),
            BroadcastEntity(
                id = "bc_2",
                authorUserId = "user_elena",
                authorName = "Elena Vance",
                authorAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
                title = "Flash Sale: Nebula's Edge $0.99 for 48h",
                message = "To celebrate the ARC opening for Singularity's Child, Nebula's Edge is discounted to $0.99 on Amazon Kindle today!",
                type = BroadcastType.PRICE_DROP,
                actionUrl = "https://amazon.com/dp/B08ELENA02",
                sentAt = "Yesterday"
            ),
            BroadcastEntity(
                id = "bc_3",
                authorUserId = "user_marcus",
                authorName = "Marcus Drake",
                authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                title = "New ARC Slots Added!",
                message = "Opened 25 additional ARC slots for The Bone Alchemist after high demand. First come, first served!",
                type = BroadcastType.ARC_OPENING,
                actionUrl = "",
                sentAt = "2 days ago"
            )
        )
        dao.insertBroadcasts(broadcasts)

        // Notifications
        val notifications = listOf(
            NotificationEntity(
                id = "notif_1",
                type = NotificationType.ARC_APPROVED,
                title = "ARC Application Approved!",
                message = "Your ARC for 'The Last Chronomancer' is ready to download. Tap to access your EPUB file.",
                targetId = "arc_chrono",
                isRead = false,
                timeAgo = "2h ago"
            ),
            NotificationEntity(
                id = "notif_2",
                type = NotificationType.REVIEW_DEADLINE,
                title = "Review Deadline Approaching (48h)",
                message = "Reminder: your review for 'Hearts in Orbit' is due in 2 days. Help indie authors launch strong!",
                targetId = "arc_hearts_orbit",
                isRead = false,
                timeAgo = "5h ago"
            ),
            NotificationEntity(
                id = "notif_3",
                type = NotificationType.VOICE_ROOM_STARTED,
                title = "Live Reading Session Started",
                message = "'Indie Sci-Fi Explorers' just started a live voice room. 14 members are tuned in.",
                targetId = "club_scifi_explorers",
                isRead = true,
                timeAgo = "Yesterday"
            ),
            NotificationEntity(
                id = "notif_4",
                type = NotificationType.AUTHOR_BROADCAST,
                title = "Author Broadcast from Ray K. Vance",
                message = "Ray just announced: 'Cover Reveal & Launch Date! Check out the alternate dust jacket.'",
                targetId = "user_ray",
                isRead = true,
                timeAgo = "Yesterday"
            ),
            NotificationEntity(
                id = "notif_5",
                type = NotificationType.SHELFMATE_REQUEST,
                title = "New Shelfmate Request",
                message = "Jamie Miller wants to connect as your reading Shelfmate.",
                targetId = "user_jamie",
                isRead = true,
                timeAgo = "3 days ago"
            )
        )
        dao.insertNotifications(notifications)

        // Follows
        val follows = listOf(
            FollowEntity(
                id = "fol_1",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                followerUserId = "user_priya",
                followerName = "Priya Sharma",
                followerEmail = "priya.reads@gmail.com",
                emailConsent = true,
                createdAt = "Aug 12, 2026"
            ),
            FollowEntity(
                id = "fol_2",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                followerUserId = "user_jamie",
                followerName = "Jamie Miller",
                followerEmail = "jamie.m@outlook.com",
                emailConsent = true,
                createdAt = "Aug 15, 2026"
            ),
            FollowEntity(
                id = "fol_3",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                followerUserId = "user_jordan",
                followerName = "Jordan Hayes",
                followerEmail = "jordan.booktok@gmail.com",
                emailConsent = true,
                createdAt = "Aug 20, 2026"
            ),
            FollowEntity(
                id = "fol_4",
                authorUserId = "user_ray",
                authorName = "Ray K. Vance",
                followerUserId = "user_alex",
                followerName = "Alex Rivera",
                followerEmail = "alex.rivera99@gmail.com",
                emailConsent = false,
                createdAt = "Aug 22, 2026"
            )
        )
        dao.insertFollows(follows)

        // Book Logs for Reader
        val bookLogs = listOf(
            BookLogEntity(
                id = "log_1",
                userId = "user_ray",
                title = "The Last Chronomancer (Author Proof)",
                author = "Ray K. Vance",
                coverUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3edd4",
                rating = 5,
                notes = "Final ARC proof completed. Pacing in Ch 14 smoothed out.",
                dateCompleted = "Aug 28, 2026",
                genre = "Fantasy"
            ),
            BookLogEntity(
                id = "log_2",
                userId = "user_ray",
                title = "Nebula's Edge",
                author = "Elena Vance",
                coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
                rating = 5,
                notes = "Stellar sci-fi thriller. Great ship combat mechanics.",
                dateCompleted = "Aug 15, 2026",
                genre = "Sci-Fi"
            ),
            BookLogEntity(
                id = "log_3",
                userId = "user_ray",
                title = "A Lavender Scented Murder",
                author = "Clara Sterling",
                coverUrl = "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6",
                rating = 4,
                notes = "Cozy and charming afternoon read.",
                dateCompleted = "Aug 02, 2026",
                genre = "Mystery"
            )
        )
        dao.insertBookLogs(bookLogs)
        // Seed Bookmark Transactions
        val bookmarkTransactions = listOf(
            BookmarkTransactionEntity(
                id = "tx_1",
                userId = "user_ray",
                title = "ARC Review Published",
                note = "Reviewed 'Nebula's Edge' with 5 stars & verified badge",
                bookmarksAmount = 150,
                type = "EARNED",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                iconEmoji = "✍️"
            ),
            BookmarkTransactionEntity(
                id = "tx_2",
                userId = "user_ray",
                title = "14-Day Reading Streak Bonus",
                note = "Maintained daily reading streak milestone (+25% boost)",
                bookmarksAmount = 100,
                type = "EARNED",
                timestamp = System.currentTimeMillis() - 86400000L * 3,
                iconEmoji = "🔥"
            ),
            BookmarkTransactionEntity(
                id = "tx_3",
                userId = "user_ray",
                title = "Read 50 Pages Milestone",
                note = "In-app FolioReader continuous reading reward",
                bookmarksAmount = 60,
                type = "EARNED",
                timestamp = System.currentTimeMillis() - 86400000L * 4,
                iconEmoji = "📖"
            ),
            BookmarkTransactionEntity(
                id = "tx_4",
                userId = "user_ray",
                title = "Daily Club Discussion",
                note = "Posted in Sci-Fi Explorers Chapter discussion",
                bookmarksAmount = 25,
                type = "EARNED",
                timestamp = System.currentTimeMillis() - 86400000L * 1,
                iconEmoji = "💬"
            ),
            BookmarkTransactionEntity(
                id = "tx_5",
                userId = "user_ray",
                title = "Fast-Pass ARC Approval Redeemed",
                note = "Skipped application queue for 'The Last Chronomancer'",
                bookmarksAmount = -250,
                type = "REDEEMED",
                timestamp = System.currentTimeMillis() - 86400000L * 5,
                iconEmoji = "⚡"
            )
        )
        dao.insertBookmarkTransactions(bookmarkTransactions)

        // Seed Bookmark Redemptions
        val bookmarkRedemptions = listOf(
            BookmarkRedemptionEntity(
                id = "red_1",
                userId = "user_ray",
                rewardId = "rew_fastpass",
                rewardTitle = "Fast-Pass ARC Instant Approval",
                costBookmarks = 250,
                redeemedTimestamp = System.currentTimeMillis() - 86400000L * 5,
                redemptionCode = "FASTPASS-77A9-RAY",
                iconEmoji = "⚡",
                status = "ACTIVE"
            ),
            BookmarkRedemptionEntity(
                id = "red_2",
                userId = "user_ray",
                rewardId = "rew_enamel_bookmark",
                rewardTitle = "Limited Edition Shelfmates Enamel Bookmark",
                costBookmarks = 800,
                redeemedTimestamp = System.currentTimeMillis() - 86400000L * 12,
                redemptionCode = "SWAG-ENAMEL-4992",
                iconEmoji = "🔖",
                status = "CLAIMED"
            )
        )
        dao.insertBookmarkRedemptions(bookmarkRedemptions)

        // Seed Custom Shelves (Virtual Bookshelves)
        val defaultShelves = listOf(
            CustomShelfEntity(
                id = "shelf_user_ray_currently_reading",
                userId = "user_ray",
                name = BookShelfCategory.CURRENTLY_READING,
                iconEmoji = "📖",
                isDefault = true,
                createdAt = System.currentTimeMillis() - 86400000L * 30
            ),
            CustomShelfEntity(
                id = "shelf_user_ray_to_read",
                userId = "user_ray",
                name = BookShelfCategory.TO_READ,
                iconEmoji = "🔖",
                isDefault = true,
                createdAt = System.currentTimeMillis() - 86400000L * 30
            ),
            CustomShelfEntity(
                id = "shelf_user_ray_finished",
                userId = "user_ray",
                name = BookShelfCategory.FINISHED,
                iconEmoji = "✅",
                isDefault = true,
                createdAt = System.currentTimeMillis() - 86400000L * 30
            ),
            CustomShelfEntity(
                id = "shelf_user_ray_scifi_favorites",
                userId = "user_ray",
                name = "Sci-Fi Favorites",
                iconEmoji = "🚀",
                isDefault = false,
                createdAt = System.currentTimeMillis() - 86400000L * 20
            )
        )
        dao.insertCustomShelves(defaultShelves)

        // Seed Saved Books (Virtual Bookshelf)
        val savedBooks = listOf(
            SavedBookEntity(
                id = "saved_user_ray_dune",
                userId = "user_ray",
                googleBooksId = "B1h0DwAAQBAJ",
                title = "Dune",
                authors = "Frank Herbert",
                coverUrl = "https://books.google.com/books/content?id=B1h0DwAAQBAJ&printsec=frontcover&img=1&zoom=1",
                category = BookShelfCategory.CURRENTLY_READING,
                description = "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling an inhospitable world.",
                genre = "Science Fiction",
                pageCount = 688,
                rating = 4.7f,
                isbn13 = "9780441013593",
                notes = "Loving the world-building and political intrigue so far on Arrakis!",
                personalRating = 5,
                savedAt = System.currentTimeMillis() - 86400000L * 3
            ),
            SavedBookEntity(
                id = "saved_user_ray_project_hail_mary",
                userId = "user_ray",
                googleBooksId = "x3GzDwAAQBAJ",
                title = "Project Hail Mary",
                authors = "Andy Weir",
                coverUrl = "https://books.google.com/books/content?id=x3GzDwAAQBAJ&printsec=frontcover&img=1&zoom=1",
                category = BookShelfCategory.TO_READ,
                description = "Ryland Grace is the sole survivor on a desperate, last-chance mission—and if he fails, humanity and the earth itself are doomed.",
                genre = "Science Fiction",
                pageCount = 496,
                rating = 4.8f,
                isbn13 = "9780593135204",
                notes = "Recommended by fellow readers in the Sci-Fi Guild club.",
                personalRating = 0,
                savedAt = System.currentTimeMillis() - 86400000L * 6
            ),
            SavedBookEntity(
                id = "saved_user_ray_name_of_the_wind",
                userId = "user_ray",
                googleBooksId = "37nUDwAAQBAJ",
                title = "The Name of the Wind",
                authors = "Patrick Rothfuss",
                coverUrl = "https://books.google.com/books/content?id=37nUDwAAQBAJ&printsec=frontcover&img=1&zoom=1",
                category = BookShelfCategory.FINISHED,
                description = "The riveting first-person narrative of Kvothe, a young man who grows to be the most notorious wizard his world has ever seen.",
                genre = "Fantasy",
                pageCount = 662,
                rating = 4.9f,
                isbn13 = "9780756404741",
                notes = "Masterpiece storytelling with sublime poetic prose.",
                personalRating = 5,
                savedAt = System.currentTimeMillis() - 86400000L * 15
            ),
            SavedBookEntity(
                id = "saved_user_ray_hyperion",
                userId = "user_ray",
                googleBooksId = "5p_4DwAAQBAJ",
                title = "Hyperion",
                authors = "Dan Simmons",
                coverUrl = "https://books.google.com/books/content?id=5p_4DwAAQBAJ&printsec=frontcover&img=1&zoom=1",
                category = "Sci-Fi Favorites",
                description = "On the world called Hyperion, beyond the reach of galactic law, waits a creature called the Shrike.",
                genre = "Science Fiction",
                pageCount = 482,
                rating = 4.6f,
                isbn13 = "9780553283686",
                notes = "A towering space opera masterpiece.",
                personalRating = 5,
                savedAt = System.currentTimeMillis() - 86400000L * 18
            )
        )
        dao.insertSavedBooks(savedBooks)

        // Atomic Shelf Command Centre Analytics (Google Apps Script Webhook)
        val asAnalytics = listOf(
            AtomicShelfAnalyticsEntity(
                id = "as_ray",
                authorId = "user_ray",
                authorName = "Ray K. Vance",
                asClientId = "AS-CLI-8492",
                period = "September 2026 Promo Campaign",
                newsletterPlacements = 3,
                newsletterSubscribersReached = 48200,
                tiktokViews = 142500,
                ytViews = 38400,
                topVideoUrl = "https://tiktok.com/@atomicshelf/video/lastchronomancer",
                syncedAt = System.currentTimeMillis() - 3600000L * 2,
                syncStatus = "Active Synced (Apps Script Webhook)"
            ),
            AtomicShelfAnalyticsEntity(
                id = "as_elena",
                authorId = "user_elena",
                authorName = "Elena Vance",
                asClientId = "AS-CLI-7120",
                period = "September 2026 Promo Campaign",
                newsletterPlacements = 4,
                newsletterSubscribersReached = 62000,
                tiktokViews = 215000,
                ytViews = 74300,
                topVideoUrl = "https://tiktok.com/@atomicshelf/video/nebulaedge",
                syncedAt = System.currentTimeMillis() - 3600000L * 5,
                syncStatus = "Active Synced (Apps Script Webhook)"
            ),
            AtomicShelfAnalyticsEntity(
                id = "as_marcus",
                authorId = "user_marcus",
                authorName = "Marcus Drake",
                asClientId = "AS-CLI-3904",
                period = "September 2026 Promo Campaign",
                newsletterPlacements = 2,
                newsletterSubscribersReached = 31500,
                tiktokViews = 89400,
                ytViews = 19200,
                topVideoUrl = "https://tiktok.com/@atomicshelf/video/bonetitan",
                syncedAt = System.currentTimeMillis() - 3600000L * 8,
                syncStatus = "Active Synced (Apps Script Webhook)"
            )
        )
        dao.insertAllAtomicShelfAnalytics(asAnalytics)

        // Redemption Requests for Admin Queue & Economy Health
        val redemptionRequests = listOf(
            RedemptionRequestEntity(
                id = "redreq_01",
                userId = "user_priya",
                userName = "Priya Sharma",
                userEmail = "priya.reads@gmail.com",
                catalogueItemId = "rew_tote",
                catalogueItemName = "Shelfmates Canvas Tote Bag",
                costBookmarks = 500,
                category = "MERCH",
                status = "PROCESSING",
                notes = "Organic cotton tote batch. Reader requested gift wrapping.",
                shippingAddress = "742 Evergreen Terrace, Apt 4B, Springfield OR 97477",
                createdAt = System.currentTimeMillis() - 86400000L * 1
            ),
            RedemptionRequestEntity(
                id = "redreq_02",
                userId = "user_jamie",
                userName = "Jamie Miller",
                userEmail = "jamie.m@outlook.com",
                catalogueItemId = "rew_stickers",
                catalogueItemName = "Shelfmates Bookmarks & Sticker Set",
                costBookmarks = 250,
                category = "MERCH",
                status = "FULFILLED",
                notes = "Dispatched via USPS First Class Ground.",
                shippingAddress = "1204 Pine Valley Rd, Austin TX 78704",
                trackingCode = "9400111899223194883102",
                createdAt = System.currentTimeMillis() - 86400000L * 4,
                fulfilledAt = System.currentTimeMillis() - 86400000L * 2
            ),
            RedemptionRequestEntity(
                id = "redreq_03",
                userId = "user_priya",
                userName = "Priya Sharma",
                userEmail = "priya.reads@gmail.com",
                catalogueItemId = "rew_bookplate",
                catalogueItemName = "Signed Digital Bookplate",
                costBookmarks = 200,
                category = "AUTHOR_PERK",
                status = "PENDING",
                notes = "Inscription: 'To Priya, keep charting the stars! - Elena Vance'",
                shippingAddress = "Digital delivery to priya.reads@gmail.com",
                createdAt = System.currentTimeMillis() - 3600000L * 12
            )
        )
        dao.insertRedemptionRequests(redemptionRequests)
    }
}