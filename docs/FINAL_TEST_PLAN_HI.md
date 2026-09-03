# Final test plan

इन tests को Samsung A55, Samsung M31s और tablet पर चलाएँ:

1. User A login करके expense, income, budget और shopping list बनाएँ। User B login पर User A का data नहीं दिखना चाहिए।
2. User A फिर login करे; उसी का data वापस दिखना चाहिए।
3. App background करने के बाद internet on रखें; दूसरे phone पर User A login करके cloud restore जाँचें।
4. एक 10-item bill बनाएँ, photo/PDF जोड़ें, Expense History से bill और सभी items खोलें।
5. Income में पुरानी तारीख चुनकर Reports custom date range में verify करें।
6. Reports में 1, 2, 3, 6, 12 months और From–To filters जाँचें।
7. एक ही item अलग महीनों/rates पर जोड़कर छह महीने की rate comparison जाँचें।
8. Category/Unit selection, custom category और custom item जाँचें।
9. Budget save करके Dashboard progress और category budget verify करें।
10. Shopping list create/edit/purchased status और Text/CSV/PDF export जाँचें।
11. Password reset, logout, PIN enable/change/disable और fingerprint unlock जाँचें।
12. Font size Default तथा Large, portrait/landscape और tablet split screen में clipping/overflow जाँचें।

Release से पहले सभी tests पर PASS screenshot रखें।
