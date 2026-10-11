package com.example.data

data class KenyaLocation(
    val county: String,
    val area: String
) {
    val displayName: String get() = "$county - $area"
}

object KenyanRegions {
    val ALL_LOCATIONS: List<KenyaLocation> = listOf(
        // 1. Nairobi
        KenyaLocation("Nairobi", "Kilimani"),
        KenyaLocation("Nairobi", "Westlands"),
        KenyaLocation("Nairobi", "Embakasi"),
        KenyaLocation("Nairobi", "Karen"),
        KenyaLocation("Nairobi", "Lavington"),
        KenyaLocation("Nairobi", "Kileleshwa"),
        KenyaLocation("Nairobi", "Roysambu"),
        KenyaLocation("Nairobi", "Kasarani"),
        KenyaLocation("Nairobi", "South C"),
        KenyaLocation("Nairobi", "South B"),
        KenyaLocation("Nairobi", "Parklands"),
        KenyaLocation("Nairobi", "Hurlingham"),
        KenyaLocation("Nairobi", "Lang'ata"),
        KenyaLocation("Nairobi", "Ngara"),
        KenyaLocation("Nairobi", "Eastleigh"),
        KenyaLocation("Nairobi", "Donholm"),
        KenyaLocation("Nairobi", "Buruburu"),
        KenyaLocation("Nairobi", "Pangani"),
        KenyaLocation("Nairobi", "Dagoretti"),
        KenyaLocation("Nairobi", "Ruaraka"),
        KenyaLocation("Nairobi", "Kahawa West"),

        // 2. Mombasa
        KenyaLocation("Mombasa", "Nyali"),
        KenyaLocation("Mombasa", "Bamburi"),
        KenyaLocation("Mombasa", "Tudor"),
        KenyaLocation("Mombasa", "Likoni"),
        KenyaLocation("Mombasa", "Changamwe"),
        KenyaLocation("Mombasa", "Mvita (CBD)"),
        KenyaLocation("Mombasa", "Kisauni"),
        KenyaLocation("Mombasa", "Shanzu"),
        KenyaLocation("Mombasa", "Mikindani"),

        // 3. Kiambu
        KenyaLocation("Kiambu", "Thika"),
        KenyaLocation("Kiambu", "Ruiru"),
        KenyaLocation("Kiambu", "Kikuyu"),
        KenyaLocation("Kiambu", "Kiambu Town"),
        KenyaLocation("Kiambu", "Limuru"),
        KenyaLocation("Kiambu", "Juja"),
        KenyaLocation("Kiambu", "Githunguri"),
        KenyaLocation("Kiambu", "Kabete"),
        KenyaLocation("Kiambu", "Karuri"),

        // 4. Nakuru
        KenyaLocation("Nakuru", "Nakuru City"),
        KenyaLocation("Nakuru", "Naivasha"),
        KenyaLocation("Nakuru", "Gilgil"),
        KenyaLocation("Nakuru", "Njoro"),
        KenyaLocation("Nakuru", "Molo"),
        KenyaLocation("Nakuru", "Bahati"),

        // 5. Kisumu
        KenyaLocation("Kisumu", "Milimani"),
        KenyaLocation("Kisumu", "Mamboleo"),
        KenyaLocation("Kisumu", "Kisumu CBD"),
        KenyaLocation("Kisumu", "Nyalenda"),
        KenyaLocation("Kisumu", "Kondele"),
        KenyaLocation("Kisumu", "Maseno"),

        // 6. Uasin Gishu (Eldoret)
        KenyaLocation("Uasin Gishu", "Eldoret CBD"),
        KenyaLocation("Uasin Gishu", "Langas"),
        KenyaLocation("Uasin Gishu", "Kapseret"),
        KenyaLocation("Uasin Gishu", "Pioneer"),
        KenyaLocation("Uasin Gishu", "Huruma"),
        KenyaLocation("Uasin Gishu", "Moiben"),

        // 7. Machakos
        KenyaLocation("Machakos", "Machakos Town"),
        KenyaLocation("Machakos", "Syokimau"),
        KenyaLocation("Machakos", "Athi River"),
        KenyaLocation("Machakos", "Mavoko"),
        KenyaLocation("Machakos", "Kangundo"),
        KenyaLocation("Machakos", "Tala"),
        KenyaLocation("Machakos", "Matuu"),

        // 8. Kajiado
        KenyaLocation("Kajiado", "Kitengela"),
        KenyaLocation("Kajiado", "Ongata Rongai"),
        KenyaLocation("Kajiado", "Ngong"),
        KenyaLocation("Kajiado", "Kajiado Town"),
        KenyaLocation("Kajiado", "Loitokitok"),

        // 9. Meru
        KenyaLocation("Meru", "Meru Town"),
        KenyaLocation("Meru", "Maua"),
        KenyaLocation("Meru", "Nkubu"),
        KenyaLocation("Meru", "Timau"),

        // 10. Nyeri
        KenyaLocation("Nyeri", "Nyeri Town"),
        KenyaLocation("Nyeri", "Karatina"),
        KenyaLocation("Nyeri", "Othaya"),
        KenyaLocation("Nyeri", "Mukurweini"),

        // 11. Kakamega
        KenyaLocation("Kakamega", "Kakamega Town"),
        KenyaLocation("Kakamega", "Mumias"),
        KenyaLocation("Kakamega", "Lugari"),
        KenyaLocation("Kakamega", "Shinyalu"),

        // 12. Kisii
        KenyaLocation("Kisii", "Kisii Town"),
        KenyaLocation("Kisii", "Suneka"),
        KenyaLocation("Kisii", "Ogembo"),
        KenyaLocation("Kisii", "Nyanchwa"),

        // 13. Kilifi
        KenyaLocation("Kilifi", "Malindi"),
        KenyaLocation("Kilifi", "Kilifi Town"),
        KenyaLocation("Kilifi", "Watamu"),
        KenyaLocation("Kilifi", "Mtwapa"),
        KenyaLocation("Kilifi", "Mariakani"),

        // 14. Kwale
        KenyaLocation("Kwale", "Diani Beach"),
        KenyaLocation("Kwale", "Ukunda"),
        KenyaLocation("Kwale", "Kwale Town"),
        KenyaLocation("Kwale", "Msambweni"),

        // 15. Bomet
        KenyaLocation("Bomet", "Bomet Town"),
        KenyaLocation("Bomet", "Sotik"),
        KenyaLocation("Bomet", "Litein"),

        // 16. Kericho
        KenyaLocation("Kericho", "Kericho Town"),
        KenyaLocation("Kericho", "Kipkelion"),

        // 17. Nandi
        KenyaLocation("Nandi", "Kapsabet"),
        KenyaLocation("Nandi", "Nandi Hills"),

        // 18. Trans Nzoia
        KenyaLocation("Trans Nzoia", "Kitale"),
        KenyaLocation("Trans Nzoia", "Endebess"),

        // 19. Bungoma
        KenyaLocation("Bungoma", "Bungoma Town"),
        KenyaLocation("Bungoma", "Webuye"),
        KenyaLocation("Bungoma", "Sirisia"),
        KenyaLocation("Bungoma", "Chwele"),

        // 20. Baringo
        KenyaLocation("Baringo", "Kabarnet"),
        KenyaLocation("Baringo", "Eldama Ravine"),
        KenyaLocation("Baringo", "Marigat"),

        // 21. Laikipia
        KenyaLocation("Laikipia", "Nanyuki"),
        KenyaLocation("Laikipia", "Nyahururu"),
        KenyaLocation("Laikipia", "Rumuruti"),

        // 22. Embu
        KenyaLocation("Embu", "Embu Town"),
        KenyaLocation("Embu", "Runyenjes"),

        // 23. Kirinyaga
        KenyaLocation("Kirinyaga", "Kerugoya"),
        KenyaLocation("Kirinyaga", "Sagana"),
        KenyaLocation("Kirinyaga", "Mwea"),

        // 24. Murang'a
        KenyaLocation("Murang'a", "Murang'a Town"),
        KenyaLocation("Murang'a", "Kenol"),
        KenyaLocation("Murang'a", "Maragua"),
        KenyaLocation("Murang'a", "Kangema"),

        // 25. Kitui
        KenyaLocation("Kitui", "Kitui Town"),
        KenyaLocation("Kitui", "Mwingi"),

        // 26. Makueni
        KenyaLocation("Makueni", "Wote"),
        KenyaLocation("Makueni", "Mtito Andei"),
        KenyaLocation("Makueni", "Kibwezi"),
        KenyaLocation("Makueni", "Sultan Hamud"),

        // 27. Taita Taveta
        KenyaLocation("Taita Taveta", "Voi"),
        KenyaLocation("Taita Taveta", "Taveta"),
        KenyaLocation("Taita Taveta", "Wundanyi"),

        // 28. Garissa
        KenyaLocation("Garissa", "Garissa Town"),
        KenyaLocation("Garissa", "Dadaab"),

        // 29. Wajir
        KenyaLocation("Wajir", "Wajir Town"),

        // 30. Mandera
        KenyaLocation("Mandera", "Mandera Town"),

        // 31. Marsabit
        KenyaLocation("Marsabit", "Marsabit Town"),
        KenyaLocation("Marsabit", "Moyale"),

        // 32. Isiolo
        KenyaLocation("Isiolo", "Isiolo Town"),

        // 33. Turkana
        KenyaLocation("Turkana", "Lodwar"),
        KenyaLocation("Turkana", "Kakuma"),

        // 34. West Pokot
        KenyaLocation("West Pokot", "Kapenguria"),

        // 35. Samburu
        KenyaLocation("Samburu", "Maralal"),

        // 36. Elgeyo Marakwet
        KenyaLocation("Elgeyo Marakwet", "Iten"),
        KenyaLocation("Elgeyo Marakwet", "Tambach"),

        // 37. Vihiga
        KenyaLocation("Vihiga", "Mbale"),
        KenyaLocation("Vihiga", "Luanda"),
        KenyaLocation("Vihiga", "Hamisi"),

        // 38. Busia
        KenyaLocation("Busia", "Busia Town"),
        KenyaLocation("Busia", "Malaba"),
        KenyaLocation("Busia", "Nambale"),

        // 39. Siaya
        KenyaLocation("Siaya", "Siaya Town"),
        KenyaLocation("Siaya", "Bondo"),
        KenyaLocation("Siaya", "Ugunja"),

        // 40. Homa Bay
        KenyaLocation("Homa Bay", "Homa Bay Town"),
        KenyaLocation("Homa Bay", "Mbita"),
        KenyaLocation("Homa Bay", "Oyugis"),

        // 41. Migori
        KenyaLocation("Migori", "Migori Town"),
        KenyaLocation("Migori", "Isebania"),
        KenyaLocation("Migori", "Rongo"),

        // 42. Nyamira
        KenyaLocation("Nyamira", "Nyamira Town"),
        KenyaLocation("Nyamira", "Keroka"),

        // 43. Tana River
        KenyaLocation("Tana River", "Hola"),
        KenyaLocation("Tana River", "Garsen"),

        // 44. Lamu
        KenyaLocation("Lamu", "Lamu Island"),
        KenyaLocation("Lamu", "Shela"),
        KenyaLocation("Lamu", "Mpeketoni"),

        // 45. Tharaka Nithi
        KenyaLocation("Tharaka Nithi", "Chuka"),
        KenyaLocation("Tharaka Nithi", "Chogoria"),

        // 46. Nandi
        KenyaLocation("Nandi", "Aldai"),

        // 47. Narok
        KenyaLocation("Narok", "Narok Town"),
        KenyaLocation("Narok", "Kilgoris"),
        KenyaLocation("Narok", "Maasai Mara")
    )
}
