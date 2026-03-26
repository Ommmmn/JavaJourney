package com.petymate.service.impl;

import com.petymate.dto.ChatbotDto;
import com.petymate.entity.ChatbotSession;
import com.petymate.entity.User;
import com.petymate.repository.ChatbotSessionRepository;
import com.petymate.repository.UserRepository;
import com.petymate.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service @RequiredArgsConstructor @Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatbotSessionRepository sessionRepo;
    private final UserRepository userRepository;

    private static final Map<String, KnowledgeEntry> KNOWLEDGE = new LinkedHashMap<>();

    static {
        KNOWLEDGE.put("mating", new KnowledgeEntry(
                "🐾 **Pet Matching (Mating Partner Finder)**\n\n" +
                "PetyMate helps you find the perfect mating partner for your pet based on species, breed, location, age, and health status.\n\n" +
                "**How it works:**\n1. Register & list your pet with photos.\n2. Browse compatible matches nearby.\n3. Send match requests.\n4. Once accepted, unlock the owner's contact (Free: 2 req/day, Basic: 10/day, Premium: unlimited).\n\n" +
                "You can filter by breed, age, city, vaccination status, pedigree, and more!",
                List.of(action("Find a Mate", "/matching"), action("List Your Pet", "/dashboard/pets/new"))));

        KNOWLEDGE.put("adoption", new KnowledgeEntry(
                "🏠 **Pet Adoption**\n\nAdopt, don't shop! Browse pets listed for adoption in your area. All adoption listings are free — connect with the pet owner once matched.\n\n" +
                "Tips:\n- Look for vaccinated pets\n- Check health status\n- Arrange a home visit before adopting",
                List.of(action("Browse Adoptions", "/shop?type=ADOPTION"), action("List for Adoption", "/dashboard/pets/new"))));

        KNOWLEDGE.put("buy", new KnowledgeEntry(
                "🛒 **Buy a Pet**\n\nFind pets for sale across India — from popular breeds like Labrador and German Shepherd to exotic species. All listings are verified.\n\n" +
                "Filter by species, breed, age, city, price range, pedigree status, and more.",
                List.of(action("Shop Now", "/shop"), action("Sell a Pet", "/dashboard/pets/new"))));

        KNOWLEDGE.put("products", new KnowledgeEntry(
                "📦 **Pet Products Store**\n\nShop food, accessories, health supplements, grooming kits, toys, and more for your pets.\n\n" +
                "- Free shipping on orders above ₹499\n- All top brands available\n- Filter by category, species, price, and ratings",
                List.of(action("Shop Products", "/products"), action("View Cart", "/cart"))));

        KNOWLEDGE.put("vet", new KnowledgeEntry(
                "🏥 **Veterinary Consultations**\n\nBook appointments with verified vets — online or at their clinic.\n\n" +
                "**How to book:**\n1. Search by city, specialization, or rating\n2. Choose a date, time, and mode (Online/Clinic)\n3. Pay via Razorpay and get instant confirmation\n\n" +
                "Cancel up to 24 hours before for a full refund.",
                List.of(action("Find a Vet", "/vets"), action("My Appointments", "/dashboard/appointments"))));

        KNOWLEDGE.put("trainer", new KnowledgeEntry(
                "🎓 **Pet Training**\n\nFind certified trainers for obedience, agility, behavior correction, puppy training, and more.\n\n" +
                "Options:\n- Individual sessions (Home Visit / Training Center / Online)\n- Training packages (buy bulk, save more)\n\n" +
                "Book and pay online. Cancel up to 24 hours before.",
                List.of(action("Find a Trainer", "/trainers"), action("My Sessions", "/dashboard/training"))));

        KNOWLEDGE.put("subscription", new KnowledgeEntry(
                "💎 **Subscription Plans**\n\n" +
                "| Feature | Free | Basic ₹199/mo | Premium ₹499/mo |\n|---|---|---|---|\n" +
                "| Browse | ✅ | ✅ | ✅ |\n| Match Requests | 2/day | 10/day | Unlimited |\n" +
                "| Contact Unlocks | ❌ | 5/month | Unlimited |\n" +
                "| Priority Listing | ❌ | ❌ | ✅ |\n| Badge | — | BASIC | PREMIUM |\n\n" +
                "Upgrade anytime — all payments via Razorpay.",
                List.of(action("View Plans", "/pricing"), action("My Subscription", "/dashboard/subscription"))));

        KNOWLEDGE.put("care", new KnowledgeEntry(
                "❤️ **Pet Care Tips**\n\n" +
                "• **Vaccination:** Follow your vet's schedule strictly. Puppies need 3 rounds of shots.\n" +
                "• **Diet:** Feed age-appropriate food. Avoid chocolate, grapes, and onion.\n" +
                "• **Exercise:** Dogs need 30-60 min/day walking. Cats need stimulation toys.\n" +
                "• **Grooming:** Brush weekly. Bath every 2-4 weeks.\n" +
                "• **Dental:** Brush teeth weekly. Use dental chews.\n" +
                "• **Mental Health:** Socialize early, keep routine consistent.",
                List.of(action("Book a Vet", "/vets"), action("Find a Trainer", "/trainers"))));

        KNOWLEDGE.put("hello", new KnowledgeEntry(
                "👋 Hi there! I'm **PetyBot**, your AI pet assistant! 🐶🐱\n\n" +
                "I can help you with:\n• Finding a mating partner\n• Buying, selling, or adopting pets\n" +
                "• Shopping for pet products\n• Booking a vet or trainer\n• Subscription plans\n• Pet care tips\n\n" +
                "Just type your question!",
                List.of(action("Find a Mate", "/matching"), action("Shop Pets", "/shop"),
                        action("Vet Booking", "/vets"), action("View Plans", "/pricing"))));

        KNOWLEDGE.put("help", KNOWLEDGE.get("hello"));
    }

    @Override
    @Transactional
    public ChatbotDto.MessageResponse processMessage(ChatbotDto.MessageRequest request) {
        String sessionToken = request.getSessionToken();
        ChatbotSession session;

        if (sessionToken == null || sessionToken.isBlank()) {
            sessionToken = UUID.randomUUID().toString();
            session = ChatbotSession.builder()
                    .sessionToken(sessionToken)
                    .messagesJson("[]")
                    .build();
            if (request.getUserId() != null) {
                User user = userRepository.findById(request.getUserId()).orElse(null);
                session.setUser(user);
            }
            sessionRepo.save(session);
        } else {
            session = sessionRepo.findBySessionToken(sessionToken).orElse(null);
            if (session == null) {
                sessionToken = UUID.randomUUID().toString();
                session = ChatbotSession.builder().sessionToken(sessionToken).messagesJson("[]").build();
                sessionRepo.save(session);
            }
        }

        String msg = request.getMessage().toLowerCase().trim();
        String intent = detectIntent(msg);
        KnowledgeEntry entry = KNOWLEDGE.getOrDefault(intent, KNOWLEDGE.get("hello"));

        return ChatbotDto.MessageResponse.builder()
                .sessionToken(sessionToken)
                .reply(entry.reply)
                .intent(intent)
                .suggestedActions(entry.actions)
                .build();
    }

    private String detectIntent(String msg) {
        if (msg.contains("mate") || msg.contains("mating") || msg.contains("match") || msg.contains("breed"))
            return "mating";
        if (msg.contains("adopt") || msg.contains("adoption") || msg.contains("rescue"))
            return "adoption";
        if (msg.contains("buy") || msg.contains("sell") || msg.contains("purchase") || msg.contains("shop pet"))
            return "buy";
        if (msg.contains("product") || msg.contains("food") || msg.contains("accessories") || msg.contains("toy"))
            return "products";
        if (msg.contains("vet") || msg.contains("doctor") || msg.contains("clinic") || msg.contains("health") || msg.contains("vaccine") || msg.contains("sick"))
            return "vet";
        if (msg.contains("train") || msg.contains("obedience") || msg.contains("behavior") || msg.contains("agility"))
            return "trainer";
        if (msg.contains("plan") || msg.contains("subscri") || msg.contains("premium") || msg.contains("basic") || msg.contains("pric") || msg.contains("upgrade"))
            return "subscription";
        if (msg.contains("care") || msg.contains("tip") || msg.contains("diet") || msg.contains("groom") || msg.contains("exercise"))
            return "care";
        if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey") || msg.contains("start"))
            return "hello";
        return "hello";
    }

    private static ChatbotDto.SuggestedAction action(String label, String url) {
        return ChatbotDto.SuggestedAction.builder().label(label).url(url).build();
    }

    private record KnowledgeEntry(String reply, List<ChatbotDto.SuggestedAction> actions) {}
}
