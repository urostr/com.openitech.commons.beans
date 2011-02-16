INSERT
INTO
    RPPCache.[dbo].[CACHE:RPP:PP]
    (
        [PPID],
        [PPTipID],
        [MaticnaStevilkaPodjetja],
        [ValidFrom],
        [ValidTo],
        [ChangedBy],
        [ChangedOn],
        [VersionID],
        [ID_PP],
        [PPTipi_Opis],
        [Ime],
        [Priimek],
        [Naziv],
        [ImeID],
        [PriimekID],
        [NazivID],
        [Naziv_IzvorID],
        [Naziv_Izvor],
        [Ulica],
        [HisnaStevilka],
        [PostnaStevilka],
        [Posta],
        [Naselje],
        [TipNaslova_Opis],
        [PPIzvori_Izvor],
        [PPID_SN],
        [PPNaslovID_SN],
        [hs_mid_PP_SN],
        [hs_hd_PP_SN],
        [na_mid_PP_SN],
        [na_uime_PP_SN],
        [ul_mid_PP_SN],
        [ul_uime_PP_SN],
        [pt_mid_PP_SN],
        [pt_id_PP_SN],
        [pt_uime_PP_SN],
        [PPID_ZN],
        [PPNaslovID_ZN],
        [hs_mid_PP_ZN],
        [hs_hd_PP_ZN],
        [na_mid_PP_ZN],
        [na_uime_PP_ZN],
        [ul_mid_PP_ZN],
        [ul_uime_PP_ZN],
        [pt_mid_PP_ZN],
        [pt_id_PP_ZN],
        [pt_uime_PP_ZN],
        [PPID_NT],
        [PPNaslovID_NT],
        [hs_mid_PP_NT],
        [hs_hd_PP_NT],
        [na_mid_PP_NT],
        [na_uime_PP_NT],
        [ul_mid_PP_NT],
        [ul_uime_PP_NT],
        [pt_mid_PP_NT],
        [pt_id_PP_NT],
        [pt_uime_PP_NT],
        [PPID_NP],
        [PPNaslovID_NP],
        [hs_mid_PP_NP],
        [hs_hd_PP_NP],
        [na_mid_PP_NP],
        [na_uime_PP_NP],
        [ul_mid_PP_NP],
        [ul_uime_PP_NP],
        [pt_mid_PP_NP],
        [pt_id_PP_NP],
        [pt_uime_PP_NP],
        [PPID_SL],
        [PPNaslovID_SL],
        [hs_mid_PP_SL],
        [hs_hd_PP_SL],
        [na_mid_PP_SL],
        [na_uime_PP_SL],
        [ul_mid_PP_SL],
        [ul_uime_PP_SL],
        [pt_mid_PP_SL],
        [pt_id_PP_SL],
        [pt_uime_PP_SL],
        [Telefon_KontaktID],
        [Telefon],
        [TelefonOmrezna],
        [TelefonStevilka],
        [GSM_KontaktID],
        [GSM],
        [GSMOmrezna],
        [GSMStevilka],
        [EMAIL_KontaktID],
        [Email],
        [Telefon_Izvor],
        [GSM_Izvor],
        [Email_Izvor],
        [Vloga_Opis],
        [TIP_OSEBE],
        [DATUM_ROJSTVA],
        [DATUM_SMRTI],
        [TAX_ID]
    )
    (
SELECT DISTINCT 
PP.PPID,
PP.PPTipID,
PP.MaticnaStevilkaPodjetja,
PP.ValidFrom,
PP.ValidTo,
PP.ChangedBy,
PP.ChangedOn,
(null) as VersionID,
PP.PPID AS ID_PP,

PPTipi.Opis AS PPTipi_Opis,

(
SELECT
Imena.StringValue
FROM
[RPP].[dbo].PPVrednosti as Imena WITH (NOLOCK)
WHERE
Imena.PPVrednostID = [PPNazivi].[ImeId]
) AS Ime,
(
SELECT
Priimki.StringValue
FROM
[RPP].[dbo].PPVrednosti as Priimki WITH (NOLOCK)
WHERE
Priimki.PPVrednostID = [PPNazivi].[PriimekId]
) AS Priimek,
(
SELECT
Nazivi.StringValue
FROM
[RPP].[dbo].PPVrednosti as Nazivi WITH (NOLOCK)
WHERE
Nazivi.PPVrednostID = [PPNazivi].[NazivId]
) AS Naziv,
PPNazivi.ImeId,
PPNazivi.PriimekId,
PPNazivi.NazivId,
(SELECT TOP 1 PPIzvori.[IzvorID] FROM [RPP].[dbo].[PPIzvoriNaziva]
LEFT OUTER JOIN
[RPP].[dbo].[PPSifre]
ON
[PPSifre].PPSifraID=[PPIzvoriNaziva].PPSifraID
LEFT OUTER JOIN
[RPP].[dbo].[PPIzvori]
ON
[PPIzvori].IzvorID=[PPSifre].IzvorID
WHERE [PPIzvoriNaziva].PPNazivID=[PPNazivi].PPNazivID) AS Naziv_IzvorID,
(SELECT TOP 1 PPIzvori.Izvor FROM [RPP].[dbo].[PPIzvoriNaziva]
LEFT OUTER JOIN
[RPP].[dbo].[PPSifre]
ON
[PPSifre].PPSifraID=[PPIzvoriNaziva].PPSifraID
LEFT OUTER JOIN
[RPP].[dbo].[PPIzvori]
ON
[PPIzvori].IzvorID=[PPSifre].IzvorID
WHERE [PPIzvoriNaziva].PPNazivID=[PPNazivi].PPNazivID) AS Naziv_Izvor,

PPNaslovi.Ulica AS Ulica,
PPNaslovi.HisnaStevilka AS HisnaStevilka,
PPNaslovi.PostnaStevilka AS PostnaStevilka,
PPNaslovi.Posta AS Posta,
PPNaslovi.Naselje AS Naselje,
PPNaslovi.TipNaslova_Opis AS TipNaslova_Opis,
PPNaslovi.PPIzvori_Izvor AS PPIzvori_Izvor,

PPNasloviPending.PPID_SN,
PPNasloviPending.PPNaslovID_SN,
PPNasloviPending.hs_mid_PP_SN,
PPNasloviPending.hs_hd_PP_SN,
PPNasloviPending.na_mid_PP_SN,
PPNasloviPending.na_uime_PP_SN,
PPNasloviPending.ul_mid_PP_SN,
PPNasloviPending.ul_uime_PP_SN,
PPNasloviPending.pt_mid_PP_SN,
PPNasloviPending.pt_id_PP_SN,
PPNasloviPending.pt_uime_PP_SN,

PPNasloviPending.PPID_ZN,
PPNasloviPending.PPNaslovID_ZN,
PPNasloviPending.hs_mid_PP_ZN,
PPNasloviPending.hs_hd_PP_ZN,
PPNasloviPending.na_mid_PP_ZN,
PPNasloviPending.na_uime_PP_ZN,
PPNasloviPending.ul_mid_PP_ZN,
PPNasloviPending.ul_uime_PP_ZN,
PPNasloviPending.pt_mid_PP_ZN,
PPNasloviPending.pt_id_PP_ZN,
PPNasloviPending.pt_uime_PP_ZN,

PPNasloviPending.PPID_NT,
PPNasloviPending.PPNaslovID_NT,
PPNasloviPending.hs_mid_PP_NT,
PPNasloviPending.hs_hd_PP_NT,
PPNasloviPending.na_mid_PP_NT,
PPNasloviPending.na_uime_PP_NT,
PPNasloviPending.ul_mid_PP_NT,
PPNasloviPending.ul_uime_PP_NT,
PPNasloviPending.pt_mid_PP_NT,
PPNasloviPending.pt_id_PP_NT,
PPNasloviPending.pt_uime_PP_NT,

PPNasloviPending.PPID_NP,
PPNasloviPending.PPNaslovID_NP,
PPNasloviPending.hs_mid_PP_NP,
PPNasloviPending.hs_hd_PP_NP,
PPNasloviPending.na_mid_PP_NP,
PPNasloviPending.na_uime_PP_NP,
PPNasloviPending.ul_mid_PP_NP,
PPNasloviPending.ul_uime_PP_NP,
PPNasloviPending.pt_mid_PP_NP,
PPNasloviPending.pt_id_PP_NP,
PPNasloviPending.pt_uime_PP_NP,

PPNasloviPending.PPID_SL,
PPNasloviPending.PPNaslovID_SL,
PPNasloviPending.hs_mid_PP_SL,
PPNasloviPending.hs_hd_PP_SL,
PPNasloviPending.na_mid_PP_SL,
PPNasloviPending.na_uime_PP_SL,
PPNasloviPending.ul_mid_PP_SL,
PPNasloviPending.ul_uime_PP_SL,
PPNasloviPending.pt_mid_PP_SL,
PPNasloviPending.pt_id_PP_SL,
PPNasloviPending.pt_uime_PP_SL,

PPKontaktiZadnjiTelefon.PPKontaktID as Telefon_KontaktID,
PPKontaktiZadnjiTelefon.PPTelefon as Telefon,
PPKontaktiZadnjiTelefon.PPOmreznaTelefon as TelefonOmrezna,
PPKontaktiZadnjiTelefon.PPStevilkaTelefon as TelefonStevilka,

PPKontaktiZadnjiGSM.PPKontaktID as GSM_KontaktID,
PPKontaktiZadnjiGSM.PPGSM as GSM,
PPKontaktiZadnjiGSM.PPOmreznaGSM as GSMOmrezna,
PPKontaktiZadnjiGSM.PPStevilkaGSM as GSMStevilka,

PPKontaktiZadnjiEmail.PPKontaktID as EMAIL_KontaktID,
PPKontaktiZadnjiEmail.PPEMAIL as Email,

(SELECT TOP 1 PPIzvoriTelefon.Izvor FROM [RPP].[DBO].PPIzvoriKontakta as PPIzvoriKontaktaTelefon
LEFT OUTER JOIN
[RPP].[DBO].PPSifre as PPSifreTelefon
ON
PPSifreTelefon.PPSifraID = PPIzvoriKontaktaTelefon.PPSifraID
LEFT OUTER JOIN
[RPP].[DBO].PPIzvori as PPIzvoriTelefon
ON
PPIzvoriTelefon.IzvorID = PPSifreTelefon.IzvorID
WHERE PPIzvoriKontaktaTelefon.PPKontaktID = PPKontaktiZadnjiTelefon.PPKontaktID ) AS Telefon_Izvor,
(SELECT TOP 1 PPIzvoriGSM.Izvor FROM [RPP].[DBO].PPIzvoriKontakta as PPIzvoriKontaktaGSM
LEFT OUTER JOIN
[RPP].[DBO].PPSifre as PPSifreGSM
ON
PPSifreGSM.PPSifraID = PPIzvoriKontaktaGSM.PPSifraID
LEFT OUTER JOIN
[RPP].[DBO].PPIzvori as PPIzvoriGSM
ON
PPIzvoriGSM.IzvorID = PPSifreGSM.IzvorID
WHERE PPIzvoriKontaktaGSM.PPKontaktID = PPKontaktiZadnjiGSM.PPKontaktID ) AS GSM_Izvor,
(SELECT TOP 1 PPIzvoriEmail.Izvor FROM [RPP].[DBO].PPIzvoriKontakta as PPIzvoriKontaktaEmail
LEFT OUTER JOIN
[RPP].[DBO].PPSifre as PPSifreEmail
ON
PPSifreEmail.PPSifraID = PPIzvoriKontaktaEmail.PPSifraID
LEFT OUTER JOIN
[RPP].[DBO].PPIzvori as PPIzvoriEmail
ON
PPIzvoriEmail.IzvorID = PPSifreEmail.IzvorID
WHERE PPIzvoriKontaktaEmail.PPKontaktID = PPKontaktiZadnjiEmail.PPKontaktID ) AS Email_Izvor,

Vloga.Opis AS Vloga_Opis,
(SELECT [VariousValues].StringValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].Id = [ev_TIP_OSEBE].[ValueId]) AS [TIP_OSEBE],
(SELECT [VariousValues].DateValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].Id = [ev_DATUM_ROJSTVA].[ValueId]) AS [DATUM_ROJSTVA],
(SELECT [VariousValues].DateValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].Id = [ev_DATUM_SMRTI].[ValueId]) AS [DATUM_SMRTI],
(SELECT [VariousValues].IntValue FROM [ChangeLog].[dbo].[VariousValues] WITH (NOLOCK) WHERE [VariousValues].Id = [ev_TAX_ID].[ValueId]) AS [TAX_ID]
FROM [RPP].[dbo].PP

LEFT OUTER JOIN
[RPP].[dbo].PPTipi WITH (NOLOCK)
ON
PPTipi.PPTipID = PP.PPTipID
 LEFT OUTER  JOIN [MViewCache].[dbo].[CACHE:RPP:MAX_VALID_NAZIVI] AS PPNaziviFilter
ON
PPNaziviFilter.PPId = PP.PPID
LEFT OUTER JOIN
[RPP].[dbo].PPNazivi
ON
(
PPNaziviFilter.PPNazivID= PPNazivi.PPNazivID
)
 LEFT OUTER  JOIN [MViewCache].[dbo].[CACHE:RPP:MAX_VALID_TELEFONI] PPKontaktiZadnjiTelefon ON
PP.PPID = PPKontaktiZadnjiTelefon.PPID
 LEFT OUTER  JOIN [MViewCache].[dbo].[CACHE:RPP:MAX_VALID_GSMI] PPKontaktiZadnjiGSM ON
PP.PPID = PPKontaktiZadnjiGSM.PPID
 LEFT OUTER  JOIN [MViewCache].[dbo].[CACHE:RPP:MAX_VALID_EMAILI] PPKontaktiZadnjiEmail ON
PP.PPID = PPKontaktiZadnjiEmail.PPID
 LEFT OUTER  JOIN
(
SELECT
PPID,
MAX(PPNaslovi.PPNaslovID) AS PPNaslovID
FROM
[RPP].[DBO].PPNaslovi

WHERE
PPNaslovi.validTo IS NULL

GROUP BY
PPID
) AS PPNasloviFilter
ON
PP.PPID= PPNasloviFilter.PPID
LEFT OUTER JOIN
(
SELECT
PPNaslovi.PPNaslovID,
PPNaslovi.PPID,
PPNaslovi.hs_tip,
PPTipNaslova.Opis AS TipNaslova_Opis,
PPIzvori.Izvor AS PPIzvori_Izvor,
PPNaslovi.hs_mid,
PPNaslovi.hs_neznana_id,
PPNaslovi.validFrom,
PPNaslovi.validTo,
CAST(PPNaslovi.hs AS VARCHAR)+PPNaslovi.hd as HisnaStevilka,
(
SELECT
StringValue
FROM
rpp.dbo.PPVrednosti
WHERE
PPVrednostID = PPNaslovi.hs_ul_uime
) as Ulica,
(
SELECT
StringValue
FROM
rpp.dbo.PPVrednosti
WHERE
PPVrednostID = PPNaslovi.hs_na_uime
) as Naselje,
RTRIM
(
(
SELECT
StringValue
FROM
rpp.dbo.PPVrednosti
WHERE
PPVrednostID = PPNaslovi.hs_ul_uime
)
+ ' ' + CAST(PPNaslovi.hs AS VARCHAR)+PPNaslovi.hd
) AS UlicaHSHD,
CAST(PPNaslovi.pt_id AS VARCHAR) AS PostnaStevilka,
(
SELECT
StringValue
FROM
rpp.dbo.PPVrednosti
WHERE
PPVrednostID = PPNaslovi.pt_uime
) AS Posta
FROM
[RPP].[DBO].PPNaslovi
LEFT OUTER JOIN
[RPP].[DBO].PPTipNaslova
ON
PPTipNaslova.hs_tip = PPNaslovi.hs_tip
LEFT OUTER JOIN
(
SELECT
     PPIzvoriNaslova.*
FROM
    (
    SELECT
        [PPNaslovID],
        MAX([PPSifraID] ) AS MAX_PPSifraID
    FROM
        [dbo].[PPIzvoriNaslova]

    GROUP BY
        PPNaslovID
    ) AS max_izvornaslova
LEFT OUTER JOIN
    [RPP].[dbo].PPIzvoriNaslova
    ON
    PPIzvoriNaslova.PPSifraID = max_izvornaslova.MAX_PPSifraID
) PPIzvoriNaslova ON PPIzvoriNaslova.PPNaslovID = PPNaslovi.PPNaslovID 
LEFT OUTER JOIN
[RPP].[dbo].PPSifre
ON
PPSifre.PPSifraID = PPIzvoriNaslova.PPSifraID
LEFT OUTER JOIN
[RPP].[dbo].PPIzvori
ON
PPIzvori.IzvorID = PPSifre.IzvorID
WHERE
PPNaslovi.validTo IS NULL
) AS PPNaslovi
ON
PPNaslovi.PPNaslovID = PPNasloviFilter.PPNaslovID
 LEFT OUTER  JOIN
(
SELECT DISTINCT PPID
FROM
[RPP].[DBO].PPSifre

) AS PPSifreFilter
ON
PP.PPID= PPSifreFilter.PPID
LEFT OUTER JOIN
(
SELECT
CAST((null) AS [int]) AS [PPID],
CAST((null) AS [int]) AS [PPTipID],
CAST((null) AS [bigint]) AS [MaticnaStevilkaPodjetja],
CAST((null) AS [datetime]) AS [ValidFrom],
CAST((null) AS [datetime]) AS [ValidTo],
CAST((null) AS [int]) AS [PPID_SN],
CAST((null) AS [int]) AS [PPNaslovID_SN],
CAST((null) AS [int]) AS [hs_mid_PP_SN],
CAST((null) AS [varchar](40)) AS [hs_hd_PP_SN],
CAST((null) AS [numeric](8, 0)) AS [na_mid_PP_SN],
CAST((null) AS [varchar](108)) AS [na_uime_PP_SN],
CAST((null) AS [numeric](8, 0)) AS [ul_mid_PP_SN],
CAST((null) AS [varchar](108)) AS [ul_uime_PP_SN],
CAST((null) AS [numeric](8, 0)) AS [pt_mid_PP_SN],
CAST((null) AS [varchar](30)) AS [pt_id_PP_SN],
CAST((null) AS [varchar](108)) AS [pt_uime_PP_SN],
CAST((null) AS [int]) AS [PPID_ZN],
CAST((null) AS [int]) AS [PPNaslovID_ZN],
CAST((null) AS [int]) AS [hs_mid_PP_ZN],
CAST((null) AS [varchar](40)) AS [hs_hd_PP_ZN],
CAST((null) AS [numeric](8, 0)) AS [na_mid_PP_ZN],
CAST((null) AS [varchar](108)) AS [na_uime_PP_ZN],
CAST((null) AS [numeric](8, 0)) AS [ul_mid_PP_ZN],
CAST((null) AS [varchar](108)) AS [ul_uime_PP_ZN],
CAST((null) AS [numeric](8, 0)) AS [pt_mid_PP_ZN],
CAST((null) AS [varchar](30)) AS [pt_id_PP_ZN],
CAST((null) AS [varchar](108)) AS [pt_uime_PP_ZN],
CAST((null) AS [int]) AS [PPID_NT],
CAST((null) AS [int]) AS [PPNaslovID_NT],
CAST((null) AS [int]) AS [hs_mid_PP_NT],
CAST((null) AS [varchar](40)) AS [hs_hd_PP_NT],
CAST((null) AS [numeric](8, 0)) AS [na_mid_PP_NT],
CAST((null) AS [varchar](108)) AS [na_uime_PP_NT],
CAST((null) AS [numeric](8, 0)) AS [ul_mid_PP_NT],
CAST((null) AS [varchar](108)) AS [ul_uime_PP_NT],
CAST((null) AS [numeric](8, 0)) AS [pt_mid_PP_NT],
CAST((null) AS [varchar](30)) AS [pt_id_PP_NT],
CAST((null) AS [varchar](108)) AS [pt_uime_PP_NT],
CAST((null) AS [int]) AS [PPID_NP],
CAST((null) AS [int]) AS [PPNaslovID_NP],
CAST((null) AS [int]) AS [hs_mid_PP_NP],
CAST((null) AS [varchar](40)) AS [hs_hd_PP_NP],
CAST((null) AS [numeric](8, 0)) AS [na_mid_PP_NP],
CAST((null) AS [varchar](108)) AS [na_uime_PP_NP],
CAST((null) AS [numeric](8, 0)) AS [ul_mid_PP_NP],
CAST((null) AS [varchar](108)) AS [ul_uime_PP_NP],
CAST((null) AS [numeric](8, 0)) AS [pt_mid_PP_NP],
CAST((null) AS [varchar](30)) AS [pt_id_PP_NP],
CAST((null) AS [varchar](108)) AS [pt_uime_PP_NP],
CAST((null) AS [int]) AS [PPID_SL],
CAST((null) AS [int]) AS [PPNaslovID_SL],
CAST((null) AS [int]) AS [hs_mid_PP_SL],
CAST((null) AS [varchar](40)) AS [hs_hd_PP_SL],
CAST((null) AS [numeric](8, 0)) AS [na_mid_PP_SL],
CAST((null) AS [varchar](108)) AS [na_uime_PP_SL],
CAST((null) AS [numeric](8, 0)) AS [ul_mid_PP_SL],
CAST((null) AS [varchar](108)) AS [ul_uime_PP_SL],
CAST((null) AS [numeric](8, 0)) AS [pt_mid_PP_SL],
CAST((null) AS [varchar](30)) AS [pt_id_PP_SL],
CAST((null) AS [varchar](108)) AS [pt_uime_PP_SL]
) AS PPNasloviPending
ON
PP.PPID= PPNasloviPending.PPID
 LEFT OUTER  JOIN
(
SELECT
PPVloga.PPID,
PPTipVloge.Opis
FROM
[RPP].[DBO].PPVloga
LEFT OUTER JOIN
[RPP].[DBO].PPTipVloge
ON
PPTipVloge.PPTipVlogeID=PPVloga.PPTipVlogeID
WHERE
validTo IS NULL
)AS Vloga
ON
PP.PPID= Vloga.PPID

LEFT OUTER JOIN [ChangeLog].[dbo].[ValuesPP] [ev_TIP_OSEBE] WITH (NOLOCK) ON (PP.[PPID] = [ev_TIP_OSEBE].[PPId] AND [ev_TIP_OSEBE].[IdPolja] = 30)
LEFT OUTER JOIN [ChangeLog].[dbo].[ValuesPP] [ev_DATUM_ROJSTVA] WITH (NOLOCK) ON (PP.[PPID] = [ev_DATUM_ROJSTVA].[PPId] AND [ev_DATUM_ROJSTVA].[IdPolja] = 29)
LEFT OUTER JOIN [ChangeLog].[dbo].[ValuesPP] [ev_DATUM_SMRTI] WITH (NOLOCK) ON (PP.[PPID] = [ev_DATUM_SMRTI].[PPId] AND [ev_DATUM_SMRTI].[IdPolja] = 46)
LEFT OUTER JOIN [ChangeLog].[dbo].[ValuesPP] [ev_TAX_ID] WITH (NOLOCK) ON (PP.[PPID] = [ev_TAX_ID].[PPId] AND [ev_TAX_ID].[IdPolja] = 28)
WHERE PP.ValidTo IS NULL
 AND PP.PPID between ? AND ?


    )