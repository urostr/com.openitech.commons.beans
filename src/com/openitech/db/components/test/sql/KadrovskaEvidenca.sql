SELECT
    Zaposleni.id,
    Zaposleni.Ime,
    Zaposleni.Priimek,
    Zaposleni.MaticnaStevilka,
    Zaposleni.DatumRojstva,
    Zaposleni.KrajRojstva,
    Zaposleni.DrzavaRojstva,
    Zaposleni.Spol,
    Zaposleni.EMSO,
    Zaposleni.DavcnaStevilka,
    Zaposleni.Banka_IDSifranta,
    Zaposleni.Banka_IDSifre,
    Zaposleni.TRR,
    Zaposleni.Izobrazba_IDSifranta,
    Zaposleni.Izobrazba_IDSifre,
    Zaposleni.StopnjaIzob_IDSifranta,
    Zaposleni.StopnjaIzob_IDSifre,
    Zaposleni.Prevoz_IDSifranta,
    Zaposleni.Prevoz_IDSifre,
    Zaposleni.StOtrokDavcnaOl,
    Zaposleni.VarstvoPodatkov,
    Zaposleni.ZdravniskiPregled,
    Zaposleni.KolNezgZavarovanje,
    HisneStevilke_SN.hs_mid  AS hs_mid_SN,
    HisneStevilke_SN.hs_hd   AS hs_hd_SN,
    HisneStevilke_SN.na_mid  AS na_mid_SN,
    HisneStevilke_SN.na_ime  AS na_ime_SN,
    HisneStevilke_SN.na_uime AS na_uime_SN,
    HisneStevilke_SN.ul_mid  AS ul_mid_SN,
    HisneStevilke_SN.ul_ime  AS ul_ime_SN,
    HisneStevilke_SN.ul_uime AS ul_uime_SN,
    HisneStevilke_SN.pt_mid  AS pt_mid_SN,
    HisneStevilke_SN.pt_id   AS pt_id_SN,
    HisneStevilke_SN.pt_ime  AS pt_ime_SN,
    HisneStevilke_SN.pt_uime AS pt_uime_SN,
    HisneStevilke_ZN.hs_mid  AS hs_mid_ZN,
    HisneStevilke_ZN.hs_hd   AS hs_hd_ZN,
    HisneStevilke_ZN.na_mid  AS na_mid_ZN,
    HisneStevilke_ZN.na_ime  AS na_ime_ZN,
    HisneStevilke_ZN.na_uime AS na_uime_ZN,
    HisneStevilke_ZN.ul_mid  AS ul_mid_ZN,
    HisneStevilke_ZN.ul_ime  AS ul_ime_ZN,
    HisneStevilke_ZN.ul_uime AS ul_uime_ZN,
    HisneStevilke_ZN.pt_mid  AS pt_mid_ZN,
    HisneStevilke_ZN.pt_id   AS pt_id_ZN,
    HisneStevilke_ZN.pt_ime  AS pt_ime_ZN,
    HisneStevilke_ZN.pt_uime AS pt_uime_ZN
FROM
    Zaposleni
LEFT OUTER JOIN
    Zaposleni_HS AS Zaposleni_HS_SN
    ON
    Zaposleni.id               = Zaposleni_HS_SN.zaposleni_id
    AND Zaposleni_HS_SN.hs_tip = 'SN'
    AND Zaposleni_HS_SN.valid_to IS NULL
LEFT OUTER JOIN
    HisneStevilke AS HisneStevilke_SN
    ON
    HisneStevilke_SN.hs_mid = Zaposleni_HS_SN.hs_mid
LEFT OUTER JOIN
    Zaposleni_HS AS Zaposleni_HS_ZN
    ON
    Zaposleni.id               = Zaposleni_HS_SN.zaposleni_id
    AND Zaposleni_HS_SN.hs_tip = 'ZN'
    AND Zaposleni_HS_SN.valid_to IS NULL
LEFT OUTER JOIN
    HisneStevilke AS HisneStevilke_ZN
    ON
    HisneStevilke_ZN.hs_mid = Zaposleni_HS_ZN.hs_mid
WHERE Zaposleni.id=1
ORDER BY     
    [Priimek],
    [Ime]
 
