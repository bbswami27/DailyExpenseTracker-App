# Ghar Budget update

इस project copy में ये बदलाव शामिल हैं:

- Expense History में हर bill की item list खुलती है।
- Bills तारीख और समय के अनुसार दिखाई देते हैं।
- किसी item पर tap करके उसकी पुरानी rate comparison देख सकते हैं।
- Expense entry में category लिखकर नई category बनाई जा सकती है।
- Shop/Vendor का optional field जोड़ा गया है।
- Bill की image या PDF attach और बाद में open कर सकते हैं।
- Add Income screen में तारीख चुन सकते हैं।
- Fresh install पर default categories अपने-आप बनती हैं।
- New Item में Category और Unit दोनों dropdown से चुन सकते हैं।
- Expense entry में चुनी Category या सभी Items—दोनों तरीके से item खोज सकते हैं।
- Budget Allocation में active categories दिखाई देती हैं और उनका मासिक budget save होता है।
- Firebase UID के अनुसार हर user का अलग local database है।
- Firebase Storage में automatic database backup और नए mobile पर restore जोड़ा गया है।
- Reports में 1/2/3/6/12 महीने, custom From–To date और All Time filter हैं।
- Reports में item-wise पिछले छह महीनों की monthly average rate comparison है।
- Reports में पिछले छह महीनों का category-wise और item-wise monthly expense भी दिखता है।
- Branding अब “घर बजट / Ghar Budget” है और नया house-wallet icon/splash जोड़ा गया है।
- Dashboard के Recent Transactions title या किसी recent expense पर tap करने से Expense History list खुलती है।
- Expense History में Edit option से खर्च की तारीख भी बदली जा सकती है।
- Income History के Edit option में income की तारीख editable रखी गई है।
- Shopping List में Photo/PDF import और attachment view जोड़ा गया है।
- Import से Item, Brand, Quantity और Unit पढ़कर Item Master से match होते हैं।
- Import review में `Item | Brand | Quantity | Unit` manual edit/delete करके final list update की जा सकती है।
- Shopping List के प्रत्येक item में Brand, Quantity, Unit और Rate manual Edit किए जा सकते हैं।
- Left navigation panel खुला होने पर Android Back button पहले panel बंद करता है।
- Printed Purchase Bill की Photo/PDF को offline scan करके Bill Number, Date, Shop और item lines पहचानी जाती हैं।
- Scan review में `Item | Qty | Unit | Rate | Amount` manual edit करने के बाद Expense form भरता है।
- Scanned bill के items Item Master से match होकर item-wise Expense में save होते हैं।
- Bill/Invoice Number Expense record और Bill Details में सुरक्षित रहता है।
- Samsung A55 और बड़े Font/Display Zoom पर Category buttons responsive full-width layout में दिखते हैं।
- Category field label छोटा किया गया है और item buttons का text single-line रखा गया है।

## Android Studio में

1. ZIP extract करके `GharBudget` folder खोलें।
2. Gradle Sync पूरा होने दें।
3. Build > Rebuild Project करें।
4. पुराने app को uninstall या Clear Data न करें। उसी application ID पर Run करें, ताकि database version 10 तक migrate हो और पुराना data बना रहे।

पुराने खर्चों में, जिनके साथ line items save नहीं हुए थे, “Item details नहीं हैं” दिखेगा। नए multi-item bills में पूरी list दिखाई देगी।
# Version 1.6.2

- Sideways bill photos के लिए 0°, 90°, 180° और 270° offline OCR auto-rotation जोड़ा।
- Bill/Invoice number की पहचान strict की; `Bill Date` अब bill number नहीं बनेगा।
- `dd-MMM-yyyy` जैसी printed bill dates पढ़ने का support जोड़ा।
- OMNIPAY/payment footer, GST, GSTIN, HSN और tax summary को vendor/item बनने से रोका।
- Supermarket POS columns (Item, Qty, UOM, Sell Price, Discount, Amount) का parser जोड़ा।
- Samsung A55 पर scanned-bill review dialog को navigation bar से ऊपर और पूरा scrollable किया।
