-- V5__seed_services.sql

INSERT INTO services (
    title,
    slug,
    category,
    short_description,
    full_description,
    points,
    image_url,
    display_order,
    published,
    archived,
    meta_title,
    meta_description,
    keywords
)
VALUES

    (
        'LGBTQ+ Support',
        'lgbtq-support',
        'Affirming Therapy',
        'A safe, affirming, and confidential space where LGBTQ+ individuals can explore identity, relationships, and wellbeing.',
        jsonb_build_array(
                'Members of the LGBTQ+ community can face unique personal, social, and emotional challenges. Whether you are exploring your identity, navigating relationships, managing anxiety or depression, or seeking support following experiences of discrimination or exclusion, therapy provides a safe and affirming environment where you can speak openly.',
                'Our approach is grounded in empathy, respect, and cultural understanding. Therapy is tailored to your individual experiences and goals, helping you develop resilience, improve emotional wellbeing, strengthen self-acceptance, and build confidence to live authentically.'
        ),
        jsonb_build_array(
                'Exploring identity',
                'Coming out support',
                'Relationship challenges',
                'Building self-acceptance'
        ),
        '/images/services/lgbtqImg.jpg',
        1,
        TRUE,
        FALSE,
        'LGBTQ+ Therapy Dublin | Root Therapy',
        'Affirming LGBTQ+ therapy in Dublin offering a safe and confidential space to explore identity, relationships and emotional wellbeing.',
        jsonb_build_array(
                'LGBTQ+ therapy Dublin',
                'LGBT counselling Dublin',
                'affirming psychotherapist',
                'gender identity therapy',
                'queer counselling Dublin'
        )
    ),

    (
        'ADHD, Autism & Neurodiversity',
        'neurodiversity',
        'Neurodiversity',
        'Neurodivergent-affirming therapy to help you navigate ADHD, autism, and everyday life with confidence.',
        jsonb_build_array(
                'Living with ADHD, autism, or another form of neurodivergence can bring unique strengths as well as challenges. Therapy provides a safe and accepting space to explore emotional regulation, executive functioning, relationships, sensory sensitivities, burnout, or understanding your diagnosis.',
                'Our neurodiversity-affirming approach recognises that different ways of thinking are not something to be fixed, but understood and supported.',
                'Together, we identify your strengths, navigate challenges, and create practical strategies that support a fulfilling and balanced life.'
        ),
        jsonb_build_array(
                'Emotional regulation',
                'Executive functioning',
                'Sensory overwhelm',
                'Self-understanding'
        ),
        '/images/services/neurodiversityImg.jpg',
        2,
        TRUE,
        FALSE,
        'ADHD & Autism Therapy Dublin | Neurodiversity Support',
        'Neurodiversity-affirming therapy in Dublin for ADHD, autism and neurodivergent adults, supporting confidence, regulation and practical coping.',
        jsonb_build_array(
                'ADHD therapy Dublin',
                'autism counselling Dublin',
                'neurodivergent therapist',
                'executive functioning support',
                'neurodiversity therapy Dublin'
        )
    ),

    (
        'Depression',
        'depression',
        'Depression Support',
        'A supportive space to explore depression, rebuild hope, and work towards improved emotional wellbeing.',
        jsonb_build_array(
                'Depression can affect every aspect of daily life, making even simple tasks feel overwhelming. It can leave you feeling persistently low, disconnected, exhausted, or without hope.',
                'Therapy provides a safe, confidential environment where you can explore your thoughts, feelings, and experiences without judgement.',
                'Our approach is tailored to your individual needs, supporting manageable steps towards improved wellbeing, renewed confidence, and greater hope for the future.'
        ),
        jsonb_build_array(
                'Low mood',
                'Loss of motivation',
                'Negative thought patterns',
                'Rebuilding confidence'
        ),
        '/images/services/depressionImg.jpg',
        3,
        TRUE,
        FALSE,
        'Depression Counselling Dublin | Root Therapy',
        'Compassionate depression counselling in Dublin to help you understand low mood, rebuild confidence and work towards improved emotional wellbeing.',
        jsonb_build_array(
                'depression counselling Dublin',
                'depression therapist Dublin',
                'low mood support',
                'therapy for depression',
                'psychotherapy Dublin'
        )
    ),

    (
        'Anxiety & Intrusive Thoughts',
        'anxiety-intrusive-thoughts',
        'Anxiety Support',
        'Learn to manage anxiety and intrusive thoughts with practical strategies that support calm, confidence, and control.',
        jsonb_build_array(
                'Anxiety and intrusive thoughts can feel overwhelming, affecting your ability to relax, concentrate, and enjoy everyday life.',
                'Therapy provides a safe, confidential space to explore your experiences without judgement and understand the factors contributing to anxiety.',
                'Together, we develop practical coping strategies that help reduce the impact of worry, panic, overthinking, and unwanted thoughts.'
        ),
        jsonb_build_array(
                'Managing overthinking',
                'Understanding intrusive thoughts',
                'Reducing panic symptoms',
                'Building emotional regulation'
        ),
        '/images/services/anxietyImg.jpg',
        4,
        TRUE,
        FALSE,
        'Anxiety Therapy Dublin | Intrusive Thoughts Counselling',
        'Therapy for anxiety, panic and intrusive thoughts in Dublin. Develop practical coping strategies and regain confidence and calm.',
        jsonb_build_array(
                'anxiety therapy Dublin',
                'intrusive thoughts therapy',
                'panic attack therapy',
                'anxiety counselling Dublin',
                'overthinking help'
        )
    ),

    (
        'Relationship Counselling',
        'relationship-counselling',
        'Relationships',
        'Strengthen communication, rebuild trust, and navigate relationship challenges in a supportive, confidential environment.',
        jsonb_build_array(
                'Every relationship experiences challenges. Relationship counselling provides a safe space to explore communication difficulties, recurring conflict, life transitions, intimacy concerns, or rebuilding trust.',
                'Therapy encourages open and honest communication while helping partners recognise unhelpful patterns and develop healthier ways of connecting.',
                'Counselling offers practical support to help you move towards a healthier and more fulfilling relationship.'
        ),
        jsonb_build_array(
                'Improving communication',
                'Resolving conflict',
                'Rebuilding trust',
                'Strengthening connection'
        ),
        '/images/services/relationshipImg.jpg',
        5,
        TRUE,
        FALSE,
        'Relationship Counselling Dublin | Root Therapy',
        'Relationship counselling in Dublin to improve communication, rebuild trust and strengthen emotional connection in a supportive environment.',
        jsonb_build_array(
                'relationship counselling Dublin',
                'couples therapy Dublin',
                'marriage counselling Dublin',
                'relationship therapist',
                'communication counselling'
        )
    ),

    (
        'Stress Management',
        'stress-management',
        'Stress Support',
        'Develop practical tools to manage stress, build resilience, and regain a healthier sense of balance in daily life.',
        jsonb_build_array(
                'Stress is a natural part of life, but when it becomes persistent or overwhelming it can affect mental health, physical wellbeing, relationships, and daily life.',
                'Together, we work to understand the sources of your stress, identify unhelpful patterns, and develop practical strategies tailored to your needs.',
                'The goal is to help you navigate future challenges with greater calm, confidence, and resilience.'
        ),
        jsonb_build_array(
                'Identifying stress triggers',
                'Reducing overwhelm',
                'Preventing burnout',
                'Building resilience'
        ),
        '/images/services/stressImg.jpg',
        6,
        TRUE,
        FALSE,
        'Stress Management Therapy Dublin | Root Therapy',
        'Manage stress, prevent burnout and build resilience with professional psychotherapy in Dublin tailored to your individual needs.',
        jsonb_build_array(
                'stress management Dublin',
                'stress therapy Dublin',
                'burnout support',
                'work stress counselling',
                'stress counselling Dublin'
        )
    ),

    (
        'Anger Management',
        'anger-management',
        'Emotional Regulation',
        'Learn healthy ways to understand anger, improve emotional regulation, and respond with greater calm and confidence.',
        jsonb_build_array(
                'Anger is a natural emotion, but when it becomes overwhelming or begins to affect relationships, work, or wellbeing, therapy can help.',
                'Together, we identify patterns and triggers while developing healthier ways to express emotions and respond to challenging situations.',
                'Therapy can support greater self-awareness, improved communication, reduced conflict, and more control over emotional responses.'
        ),
        jsonb_build_array(
                'Understanding anger triggers',
                'Managing frustration',
                'Improving communication',
                'Reducing conflict'
        ),
        '/images/services/angerImg.jpg',
        7,
        TRUE,
        FALSE,
        'Anger Management Therapy Dublin | Root Therapy',
        'Professional anger management therapy in Dublin. Understand triggers, improve emotional regulation and strengthen relationships.',
        jsonb_build_array(
                'anger management Dublin',
                'anger therapy Dublin',
                'emotional regulation therapy',
                'anger counselling',
                'managing anger'
        )
    ),

    (
        'Bereavement',
        'bereavement',
        'Grief Support',
        'Compassionate support to help you process grief, honour your loss, and move forward at your own pace.',
        jsonb_build_array(
                'Losing someone important can be one of life''s most difficult experiences. Grief affects everyone differently, and there is no right or wrong way to navigate it.',
                'Therapy provides a safe and confidential space where you can express emotions without judgement or pressure.',
                'The aim is not to move on from grief, but to move forward with compassion, resilience, and hope while keeping meaningful memories close.'
        ),
        jsonb_build_array(
                'Processing grief',
                'Managing sadness or guilt',
                'Adjusting after loss',
                'Honouring meaningful memories'
        ),
        '/images/services/bereavementImg.jpg',
        8,
        TRUE,
        FALSE,
        'Bereavement Counselling Dublin | Root Therapy',
        'Compassionate bereavement counselling in Dublin to help you process grief, cope with loss and move forward at your own pace.',
        jsonb_build_array(
                'bereavement counselling Dublin',
                'grief therapy Dublin',
                'loss counselling',
                'grief support',
                'therapy after loss'
        )
    ),

    (
        'Addiction Recovery',
        'addiction-recovery',
        'Recovery',
        'Compassionate, non-judgemental support to help you understand addiction, develop healthier coping strategies, and move towards lasting recovery.',
        jsonb_build_array(
                'Addiction can affect mental health, relationships, work, and overall wellbeing. Seeking support is a courageous first step towards change.',
                'Therapy provides a confidential space to explore the emotional, psychological, and life experiences that may contribute to addictive behaviours.',
                'Recovery is personal, and therapy is tailored to your circumstances, helping you build resilience, self-awareness, healthier coping strategies, and hope for the future.'
        ),
        jsonb_build_array(
                'Understanding addiction patterns',
                'Managing cravings',
                'Building healthier coping strategies',
                'Preventing relapse'
        ),
        '/images/services/addictionImg.jpg',
        9,
        TRUE,
        FALSE,
        'Addiction Recovery Therapy Dublin | Root Therapy',
        'Compassionate addiction recovery counselling in Dublin for alcohol, drugs, gambling and other addictive or compulsive behaviours.',
        jsonb_build_array(
                'addiction counselling Dublin',
                'addiction recovery therapy',
                'alcohol addiction counselling',
                'gambling addiction support',
                'addiction therapist Dublin'
        )
    );