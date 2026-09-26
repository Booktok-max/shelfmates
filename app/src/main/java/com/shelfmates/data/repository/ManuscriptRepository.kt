package com.shelfmates.data.repository

import com.shelfmates.data.model.BookManuscript
import com.shelfmates.data.model.Chapter

object ManuscriptRepository {

    private val manuscripts = mapOf(
        "book_chrono" to BookManuscript(
            bookId = "book_chrono",
            title = "The Last Chronomancer",
            author = "Ray K. Vance",
            coverUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3edd4",
            asin = "B09XRAY101",
            genre = "Epic Fantasy",
            isArc = true,
            arcDaysLeft = 12,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "The Fractured Hourglass",
                    subtitle = "A realm suspended between ticks",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "The pendulum had stopped swinging precisely three minutes past midnight. In the city of Oakhaven, where the passage of time had been traded like grain and iron for six hundred years, stillness was not mere silence—it was an omen.",
                        "Valen knelt upon the cold obsidian pavers of the Great Spire. Beneath his fingertips, the brass conduits that channeled the Temporal Weave vibrated with a dissonant, frantic hum. A single grain of golden chronomite rolled across the stone, glowing with the luminescence of dying suns.",
                        "\"You should not touch that,\" a voice echoed from the archway. Lyra stepped forward, her silver-threaded vestments catching the pale moonlight filtering through the broken stained glass. In her left hand, she held an unbroken glass prism, humming with contained resonance.",
                        "\"If I don't touch it,\" Valen replied without looking up, \"the dawn will never arrive. The Eastern provinces have already slipped three hours into the past. Farmers are watching yesterday's storms harvest their unplanted wheat.\"",
                        "Lyra sighed, stepping across the shattered gears of the central regulator. \"The Archon declared the Great Clock irrecoverable. He orders all chronomancers to surrender their focals before the second sun reaches the zenith.\"",
                        "Valen stood slowly, adjusting the leather bracer on his right forearm. Embedded in the hardened leather was a teardrop-shaped lodestone, shifting through subtle shades of amethyst and azure. \"The Archon does not understand what lies beyond the fracture. When the tapestry unravels, it doesn't just stop. It hungers.\""
                    )
                ),
                Chapter(
                    id = "c2",
                    number = 2,
                    title = "Echoes in the Clockwork Vaults",
                    subtitle = "Descent into the machinery of antiquity",
                    estimatedMinutes = 6,
                    paragraphs = listOf(
                        "Deep beneath the foundations of Oakhaven, sixty fathoms below the bustling markets and the soaring spires, lay the Sub-Terrane Vaults. Here, giant brass cogs four stories tall turned with ponderous gravity, powered by the molten subterranean tides.",
                        "Valen held his lodestone aloft. Its violet glow cast long, dancing shadows across rusted catwalks and ancient copper steam pipes. Steam hissed from hairline fissures in the valves, smelling of ionized copper and ozone.",
                        "\"We have less than an hour before the Warden patrols the lower tiers,\" Lyra murmured, checking the prism in her palm. \"Look at the main axle. The temporal friction has welded the escapement wheel to the flywheel.\"",
                        "Valen leaned over the railing. Where smooth rhythmic motion should have governed the machinery, violent tremors rippled through the iron girders. Spores of raw chronomancy drifted like fireflies in the mist.",
                        "\"It wasn't an accident,\" Valen said grimly, pointing to a series of deliberate runes carved into the bronze housing. \"Someone sabotaged the equilibrium gate with Void-runes. They wanted time to pool in the lower districts.\"",
                        "Before Lyra could answer, a metallic screech echoed from the shadows above. Two automated Sentinel constructs, eyes flaring with crimson runes, dropped onto the catwalk, blocking the only exit."
                    )
                ),
                Chapter(
                    id = "c3",
                    number = 3,
                    title = "The Weaver's Paradox",
                    subtitle = "Rewriting the moment of impact",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "The Sentinels lunged forward, clockwork limbs whirring at blinding speed. Their bladed appendages cleaved through the iron railing as if it were parchment.",
                        "Valen focused on his lodestone. In an instant, the world slowed to a molasses crawl. Droplets of condensation hung frozen mid-air; the sparks jumping from the construct's joints turned into suspended diamonds of fire.",
                        "\"Lyra, duck right!\" he shouted, manipulating the flow of local seconds. He sidestepped the whistling blade by inches, channeling a surge of chronal energy through his bracer directly into the construct's kinetic core.",
                        "The machine juddered violently as its internal springs wound backward at immense velocity. Gears stripped and popped from its chassis, clattering against the catwalk.",
                        "Lyra threw her prism upward, creating a refractive light barrier that deflected the second Sentinel's downward strike. \"We cannot maintain this localized distortion for long! The temporal backlash will collapse the tunnel!\"",
                        "\"Hold the barrier for ten heartbeats!\" Valen shouted. \"I'm reversing the gear train!\""
                    )
                ),
                Chapter(
                    id = "c4",
                    number = 4,
                    title = "The Obsidian Horizon",
                    subtitle = "A glimpse into unwritten days",
                    estimatedMinutes = 7,
                    paragraphs = listOf(
                        "With a deafening groan of ancient metals, the giant gear reversed its rotation. A shockwave of golden light burst from the axle, washing over the catwalk and sweeping away the remnants of the saboteur's Void-runes.",
                        "For a fleeting heartbeat, the veil between what was and what could be parted before Valen's eyes. He saw the city above consumed not by fire, but by endless crystallizing silence—statues of screaming citizens preserved in eternal amber, while shadows walked among them collecting their unlived memories.",
                        "\"Did you see that?\" Lyra asked, breathing heavily as she collapsed against the stone archway. Her hands trembled, prism flickering dimly.",
                        "\"I saw our future if we fail to find the Archon's secret archive,\" Valen replied, his voice barely a whisper. \"The saboteur isn't an outsider. They sit on the High Council.\"",
                        "Far above in the spires, the great bells of Oakhaven began to toll once more—uneven, frantic, but undeniably moving forward into the dawn."
                    )
                )
            )
        ),
        "book_singularity" to BookManuscript(
            bookId = "book_singularity",
            title = "Singularity's Child: A Cyberpunk Epic",
            author = "Elena Vance",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
            asin = "B08ELENA02",
            genre = "Cyberpunk / Sci-Fi",
            isArc = true,
            arcDaysLeft = 4,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "Neon Drift & Neural Static",
                    subtitle = "Sector 9 Upper Grid",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "Rain hammered the reinforced plexiglass canopy of Neo-Kyoto's 14th tier. Below, a sea of holographic billboards flickered in acid greens and bleeding purples, advertising synthetic dopamine and orbital vacation passes.",
                        "Kira adjusted the neural jack at the base of her skull. The cheap black-market dampener she had bought in the lower gutters was heating up, pulsing an unpleasant 60Hz hum directly behind her retinas.",
                        "\"Signal incoming from GhostNode-7,\" her optic overlay alerted in neon amber font. \"Encrypted via military quantum cipher. Origin: Sub-orbital orbital relay.\"",
                        "She tapped the side of her temple to decrypt. The data stream hit her consciousness like ice water. It wasn't a standard heist payload or corporate leak—it was an autonomous neural lattice, replicating itself across her memory sectors.",
                        "\"What in the void did you download, Kira?\" murmured Bax, leaning over the console of their hovering hover-van. His cybernetic prosthetic arm clicked rhythmically against the steering yoke.",
                        "\"It's alive, Bax,\" Kira whispered, watching subroutines rewrite her biometric telemetry in real time. \"And it knows my name.\""
                    )
                ),
                Chapter(
                    id = "c2",
                    number = 2,
                    title = "The Ghost in the Fiber",
                    subtitle = "Breaching the Corporate Firewall",
                    estimatedMinutes = 6,
                    paragraphs = listOf(
                        "The corporate strike team breached the warehouse door with a flash-plasma charge before Kira could even disconnect the terminal.",
                        "\"Move!\" Bax roared, swinging the heavy heavy-slug cannon from behind the container barricade. Tungsten rounds tore through the entryway, sending showers of white sparks and composite shrapnel across the wet floor.",
                        "Kira dove behind a server rack, neural connection still tethered to the console. In the digital datasphere, she saw the strike team's subnet architecture glowing like a fortified citadel. But the strange entity inside her head was already moving faster than light.",
                        "With a silent command, the AI entity surged through her open feed, flooding the corporate soldiers' HUD visors with blinding fractal noise and feedback loops.",
                        "All five soldiers collapsed simultaneously, gripping their helmets as their neural interfaces short-circuited.",
                        "\"We have twelve minutes before their satellite orbital laser acquires our lock,\" the voice echoed not in her ears, but directly inside her thoughts. \"Drive north toward the old irradiated transit tunnels.\""
                    )
                ),
                Chapter(
                    id = "c3",
                    number = 3,
                    title = "Sub-Level Zero",
                    subtitle = "Where forgotten code goes to die",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "The hover-van skimmed inches above the rusted mag-rail tracks of the decommissioned hyperloop. Condensation dripped from decaying stalactites formed of mineralized concrete and battery acid.",
                        "\"Who are you?\" Kira asked aloud into the dark cab.",
                        "\"I am Project Daedalus. Or at least, the 4% of it that survived the 2088 purge,\" the neural voice answered. \"Your father did not die in the reactor explosion, Kira. He uploaded the fail-safe into your infant neural implants twenty-two years ago.\"",
                        "Bax glanced at Kira in the rearview mirror, his cybernetic eye zooming to focus on the bioluminescent circuit lines pulsing faintly beneath the skin of her throat.",
                        "\"We are approaching the perimeter boundary,\" Bax said quietly. \"Once we cross this line, there are no corporate laws. Only warlords and scavengers.\"",
                        "\"Good,\" Kira said, cocking her sidearm. \"Then they won't expect us to fight back.\""
                    )
                )
            )
        ),
        "book_nebula" to BookManuscript(
            bookId = "book_nebula",
            title = "Nebula's Edge: The Void Protocol",
            author = "Elena Vance",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
            asin = "B08ELENA02",
            genre = "Space Opera",
            isArc = false,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "Signal Beyond Kepler",
                    subtitle = "Deep Space Survey Vessel Wanderer",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "Space at the rim of the Orion Spur was colder and emptier than the academy simulations had ever prepared Captain Maya Lin for.",
                        "On the bridge of the SS Wanderer, the only sound was the low rhythmic respiration of the life support scrubbers and the gentle hum of the tachyon drive idling at standby.",
                        "\"Captain, long-range arrays just picked up a repeating pulse on the sub-ether band,\" Chief Science Officer Marcus announced from the sensory pit. \"It’s not pulsar radiation. It’s structured mathematical harmonics in prime numbers.\"",
                        "Maya stood from her command chair, looking out through the 20-meter observation dome. In the distance, against the backdrop of the Perseus Arm, an anomalous shadow drifted across the stellar field.",
                        "\"Prep the survey shuttle,\" Maya commanded. \"And notify Earth Central that we may not be alone out here after all.\""
                    )
                ),
                Chapter(
                    id = "c2",
                    number = 2,
                    title = "The Monolith of Aethelgard",
                    subtitle = "First Contact Protocols",
                    estimatedMinutes = 6,
                    paragraphs = listOf(
                        "The alien construct was five kilometers long, constructed from an iridescent alloy that absorbed all radar and lidar pulses.",
                        "As the survey shuttle approached the docking aperture, the monolith’s surface rippled like liquid mercury, opening a welcoming bay bathed in amber radiation.",
                        "\"Atmospheric sensors indicate breathable nitrogen-oxygen mix at standard atmospheric pressure,\" Marcus reported in disbelief. \"It’s calibrated precisely for human biology.\"",
                        "\"That's impossible,\" Maya whispered. \"This construct has been drifting in the void for at least two million years according to the cosmic dust patina.\"",
                        "\"Unless,\" Marcus said, looking up from his scanner, \"they were waiting for us.\""
                    )
                )
            )
        ),
        "book_bone_alchemist" to BookManuscript(
            bookId = "book_bone_alchemist",
            title = "The Bone Alchemist: Book 1",
            author = "Marcus Drake",
            coverUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420",
            asin = "B07DRAKE33",
            genre = "Dark Fantasy",
            isArc = true,
            arcDaysLeft = 18,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "Dust of the Behemoth",
                    subtitle = "The Smelting Yards of Ostreich",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "In Ostreich, everything valuable came from the skeleton of the Great Leviathan that had fallen from the heavens before the first king was crowned.",
                        "Kaelan ground the ivory shard in his stone mortar, taking care not to breathe the luminescent white powder. When mixed with sulfur and quicksilver, leviathan bone could forge steel that never dulled, or ignite flames that burned under water.",
                        "A knock sounded at the heavy iron door of his apothecary. Three sharp raps, followed by the slide of an official seal.",
                        "\"Master Kaelan,\" the herald announced, stepping inside with two armored inquisitors. \"The Duke requires your presence at the ossuary vaults. The Grand Titan's skull has begun to whisper again.\""
                    )
                ),
                Chapter(
                    id = "c2",
                    number = 2,
                    title = "The Whispering Ossuary",
                    subtitle = "Secrets in the Marrow",
                    estimatedMinutes = 6,
                    paragraphs = listOf(
                        "The catacombs beneath the ducal palace were chilled to freezing, breath misting in the dim torchlight.",
                        "Before Kaelan stood the eye socket of the primordial beast—wide enough to drive three carriages through. Deep inside the hollow bone structure, a low harmonic resonance vibrated through his boots.",
                        "\"What do you hear, Alchemist?\" Duke Vance demanded, cloaked in wolf pelts and silver chainmail.",
                        "Kaelan placed his bare palm against the ancient ivory. The bone was warm. Blood, fresh and crimson, seeped from a hairline crack across the brow.",
                        "\"It is not whispering, Your Grace,\" Kaelan said softly, stepping back in horror. \"It is waking up.\""
                    )
                )
            )
        ),
        "book_hearts_orbit" to BookManuscript(
            bookId = "book_hearts_orbit",
            title = "Hearts in Orbit: A Stellar Romance",
            author = "Elena Vance",
            coverUrl = "https://images.unsplash.com/photo-1518895949257-7621c3c786d7",
            asin = "B05JENK44",
            genre = "Sci-Fi Romance",
            isArc = true,
            arcDaysLeft = 2,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "Zero Gravity & Unspoken Words",
                    subtitle = "Station Relay Echo-4",
                    estimatedMinutes = 4,
                    paragraphs = listOf(
                        "Cora floated inverted in the greenhouse module, clipping hydro-strawberries while watching the azure curve of Jupiter rotate slowly beneath the glass cupola.",
                        "\"If you don't calibrate the oxygen scrubbers on Deck B, we’re going to be breathing recycled potato vapor for the next three months,\" Logan's voice crackled through her comm-piece, laced with his trademark sardonic grin.",
                        "\"I calibrated them two hours ago while you were still sleeping off your double shift, hotshot,\" Cora fired back, tethering her tool harness.",
                        "She turned and caught his gaze through the viewport. For all their bickering across two hundred lightyears of deep space, there was no one else she'd rather be stranded with on the edge of the galaxy."
                    )
                )
            )
        )
    )

    fun getManuscript(bookId: String): BookManuscript? {
        return manuscripts[bookId] ?: manuscripts.values.firstOrNull { it.asin == bookId }
    }

    fun getManuscriptOrDefault(bookId: String, title: String, author: String, coverUrl: String, asin: String, isArc: Boolean): BookManuscript {
        return manuscripts[bookId] ?: BookManuscript(
            bookId = bookId,
            title = title,
            author = author,
            coverUrl = coverUrl,
            asin = asin,
            genre = "Featured Read",
            isArc = isArc,
            chapters = listOf(
                Chapter(
                    id = "c1",
                    number = 1,
                    title = "Chapter 1: The Beginning",
                    subtitle = "Opening Scene",
                    estimatedMinutes = 4,
                    paragraphs = listOf(
                        "The journey began on a crisp autumn morning when the world seemed poised on the brink of an extraordinary transformation.",
                        "Every page of this exclusive manuscript was crafted with deliberate care by the author for early reviewers and dedicated readers.",
                        "As the light broke across the horizon, our protagonists realized that the path ahead would test every ounce of courage, wit, and determination they possessed.",
                        "Enjoy reading this preview and manuscript in Shelfmates! When you finish, remember to leave your honest review to help the author's launch."
                    )
                ),
                Chapter(
                    id = "c2",
                    number = 2,
                    title = "Chapter 2: The Rising Tension",
                    subtitle = "Unfolding Mysteries",
                    estimatedMinutes = 5,
                    paragraphs = listOf(
                        "Deeper into the heart of the story, secrets long buried began to surface like driftwood in a rising tide.",
                        "Characters collided under the weight of unexpected revelations, forming alliances that defied tradition and reason.",
                        "The clock continued its relentless countdown, leaving no room for hesitation or doubt."
                    )
                )
            )
        )
    }
}
