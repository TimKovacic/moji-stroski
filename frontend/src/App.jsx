import { useState, useEffect } from 'react'//useState Reactu pove, katere podatke mora spremljati in prikazati
import api from './services/api'
import './App.css'

//pridobivanje danasnjega datuma
function danesnjiDatum() {
  const danes = new Date()
  const leto = danes.getFullYear()
  const mesec = String(danes.getMonth() + 1).padStart(2, '0')
  const dan = String(danes.getDate()).padStart(2, '0')

  return `${leto}-${mesec}-${dan}`
}

function App() {
  const [stroski, setStroski] = useState([])
  const [opis, setOpis] = useState('')
  const [znesek, setZnesek] = useState('')
  const [kategorija, setKategorija] = useState('Hrana')
  const [datum, setDatum] = useState(() => danesnjiDatum())
  const [urejanjeId, setUrejanjeId] = useState(null)

  const [datumOd, setDatumOd] = useState('2026-01-01')
  const [datumDo, setDatumDo] = useState(() => danesnjiDatum())

  const [analiza, setAnaliza] = useState(null)
  const [filterKategorija, setFilterKategorija] =
      useState('Vse kategorije')
  const [nalaganjeAnalize, setNalaganjeAnalize] = useState(false)
  const [napakaAnalize, setNapakaAnalize] = useState('')

  useEffect(() => {
    naloziStroske()
  }, [])

  async function naloziStroske() {
    try {
      const odgovor = await api.get('')
      setStroski(odgovor.data)
    } catch (napaka) {
      console.error('Napaka pri nalaganju stroškov:', napaka)
      alert('Stroškov ni bilo mogoče naložiti. Preveri, ali backend deluje.')
    }
  }

  async function prikaziAnalizo(event) {
    event.preventDefault()
    setNapakaAnalize('')
    setAnaliza(null)

    if (!datumOd || !datumDo) {
      setNapakaAnalize('Izberi začetni in končni datum.')
      return
    }

    if (datumOd > datumDo) {
      setNapakaAnalize('Začetni datum ne sme biti za končnim datumom.')
      return
    }

    setNalaganjeAnalize(true)

    try {
      const odgovor = await api.get('/analiza', { //poslje izbrana datuma in kategorijo
        params: {
          od: datumOd,
          do: datumDo,
          kategorija: filterKategorija,
        },
      })

      setAnaliza(odgovor.data)
    } catch (napaka) {
      console.error('Napaka pri analizi stroškov:', napaka)
      setNapakaAnalize('Analize ni bilo mogoče naložiti. Poskusi znova.')
    } finally {
      setNalaganjeAnalize(false)
    }
  }

  const skupaj = stroski.reduce( //sesteje vse zneske, da dobimo skupni veliki strosek
      (vsota, strosek) => vsota + strosek.znesek,
      0
  )


  async function shraniStrosek(event) { //doda nov strosek ali shrani spremembe obstojecega
    event.preventDefault()

    if (!opis.trim() || Number(znesek) <= 0 || !datum) {
      alert('Izpolni vsa polja in vnesi znesek, večji od 0.')
      return
    }

    const podatki = {
      opis: opis.trim(),
      znesek: Number(znesek),
      kategorija,
      datum,
    }

    try {
      if (urejanjeId !== null) {
        await api.put(`/${urejanjeId}`, podatki)
      } else {
        await api.post('', podatki)
      }

      await naloziStroske()
      ponastaviObrazec()
    } catch (napaka) {
      console.error('Napaka pri shranjevanju stroška:', napaka)
      alert('Stroška ni bilo mogoče shraniti. Preveri, ali backend deluje.')
    }
  }


  function zacniUrejanje(strosek) { //prenese podatke izbrane vrstice v obrazec
    setUrejanjeId(strosek.id)
    setOpis(strosek.opis)
    setZnesek(String(strosek.znesek))
    setKategorija(strosek.kategorija)
    setDatum(strosek.datum)
  }


  function ponastaviObrazec() {
    setUrejanjeId(null)
    setOpis('')
    setZnesek('')
    setKategorija('Hrana')
    setDatum(danesnjiDatum())
  }

  async function izbrisiStrosek(id) { //odstrani izbrani strosek
    if (!window.confirm('Ali res želiš izbrisati ta strošek?')) {
      return
    }

    try {
      await api.delete(`/${id}`)
      await naloziStroske()

      if (urejanjeId === id) {
        ponastaviObrazec()
      }
    } catch (napaka) {
      console.error('Napaka pri brisanju stroška:', napaka)
      alert('Stroška ni bilo mogoče izbrisati.')
    }
  }


  function formatirajZnesek(znesek) {
    return new Intl.NumberFormat('sl-SI', {
      style: 'currency',
      currency: 'EUR',
    }).format(znesek)
  }

  return (
      <main className="aplikacija">
        <header className="glava">
          <div className="znamka">
            <div className="znak">€</div>
            <div>
              <h1>Moji stroški</h1>
              <p>Preprost pregled osebnih financ</p>
            </div>
          </div>
          <span className="oznaka">OSEBNI PREGLED</span>
        </header>

        <section className="pozdrav">
          <p className="nadnaslov">TVOJE FINANCE, NA ENEM MESTU</p>
          <h2>Pregled stroškov</h2>
          <p>Enostavno spremljaj, kam gre tvoj denar.</p>
        </section>

        <section className="povzetek">
          <article className="kartica">
            <span className="kartica-ikona">€</span>
            <p>Skupni stroški</p>
            <strong>{formatirajZnesek(skupaj)}</strong>
            <small>Vsota vseh prikazanih stroškov</small>
          </article>

          <article className="kartica">
            <span className="kartica-ikona">#</span>
            <p>Število stroškov</p>
            <strong>{stroski.length}</strong>
            <small>Zabeleženi posamezni izdatki</small>
          </article>
        </section>

        <section className="vsebina">
          <div className="panel obrazec-panel">
            <div className="panel-glava">
              <div>
                <h3>{urejanjeId !== null ? 'Uredi strošek' : 'Dodaj strošek'}</h3>
                <p>Vnesi podrobnosti izdatka.</p>
              </div>
              <span className="plus">+</span>
            </div>

            <form onSubmit={shraniStrosek}>
              <label htmlFor="opis">Opis stroška</label>
              <input
                  id="opis"
                  value={opis}
                  onChange={(e) => setOpis(e.target.value)}
                  placeholder="Npr. Nakup živil"
                  required
              />

              <label htmlFor="znesek">Znesek (€)</label>
              <input
                  id="znesek"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={znesek}
                  onChange={(e) => setZnesek(e.target.value)}
                  placeholder="0,00"
                  required
              />

              <label htmlFor="kategorija">Kategorija</label>
              <select
                  id="kategorija"
                  value={kategorija}
                  onChange={(e) => setKategorija(e.target.value)}
              >
                <option>Hrana</option>
                <option>Prevoz</option>
                <option>Stanovanje</option>
                <option>Prosti čas</option>
                <option>Zdravje</option>
                <option>Drugo</option>
              </select>

              <label htmlFor="datum">Datum</label>
              <input
                  id="datum"
                  type="date"
                  value={datum}
                  onChange={(e) => setDatum(e.target.value)}
                  required
              />

              <button className="gumb-glavni" type="submit">
                {urejanjeId !== null ? 'Shrani spremembe' : '+ Dodaj strošek'}
              </button>

              {urejanjeId !== null && (
                  <button
                      className="gumb-preklic"
                      type="button"
                      onClick={ponastaviObrazec}
                  >
                    Prekliči urejanje
                  </button>
              )}
            </form>
          </div>

          <div className="panel tabela-panel">
            <div className="panel-glava">
              <div>
                <h3>Moji izdatki</h3>
                <p>Pregled vseh zabeleženih stroškov.</p>
              </div>
              <span className="stevec">{stroski.length} zapisov</span>
            </div>

            {stroski.length === 0 ? (
                <p className="prazno">Ni še nobenega stroška. Dodaj svojega!</p>
            ) : (
                <div className="tabela-ovoj">
                  <table>
                    <thead>
                    <tr>
                      <th>Opis</th>
                      <th>Kategorija</th>
                      <th>Datum</th>
                      <th>Znesek</th>
                      <th>Dejanja</th>
                    </tr>
                    </thead>
                    <tbody>
                    {stroski.map((strosek) => (
                        <tr key={strosek.id}>
                          <td className="opis-celica">{strosek.opis}</td>
                          <td>
                        <span className="kategorija">
                          {strosek.kategorija}
                        </span>
                          </td>
                          <td>{strosek.datum}</td>
                          <td className="znesek-celica">
                            {formatirajZnesek(strosek.znesek)}
                          </td>
                          <td>
                            <div className="dejanja">
                              <button
                                  className="gumb-uredi"
                                  type="button"
                                  onClick={() => zacniUrejanje(strosek)}
                              >
                                Uredi
                              </button>
                              <button
                                  className="gumb-izbrisi"
                                  type="button"
                                  onClick={() => izbrisiStrosek(strosek.id)}
                              >
                                Briši
                              </button>
                            </div>
                          </td>
                        </tr>
                    ))}
                    </tbody>
                  </table>
                </div>
            )}
          </div>
        </section>


        <section className="panel analiza-panel">
          <div className="panel-glava">
            <div>
              <h3>Analiza stroškov</h3>
              <p>Izberi obdobje in preveri svoje izdatke.</p>
            </div>
            <span className="plus">€</span>
          </div>

          <form className="analiza-obrazec" onSubmit={prikaziAnalizo}>
            <div className="analiza-polje">
              <label htmlFor="datumOd">Začetni datum</label>
              <input
                  id="datumOd"
                  type="date"
                  value={datumOd}
                  onChange={(e) => setDatumOd(e.target.value)}
                  required
              />
            </div>

            <div className="analiza-polje">
              <label htmlFor="datumDo">Končni datum</label>
              <input
                  id="datumDo"
                  type="date"
                  value={datumDo}
                  onChange={(e) => setDatumDo(e.target.value)}
                  required
              />
            </div>

            <div className="analiza-polje">
              <label htmlFor="filterKategorija">Kategorija</label>
              <select
                  id="filterKategorija"
                  value={filterKategorija}
                  onChange={(e) => setFilterKategorija(e.target.value)}
              >
                <option>Vse kategorije</option>
                <option>Hrana</option>
                <option>Prevoz</option>
                <option>Stanovanje</option>
                <option>Prosti čas</option>
                <option>Zdravje</option>
                <option>Drugo</option>
              </select>
            </div>

            <button className="gumb-glavni" type="submit" disabled={nalaganjeAnalize}>
              {nalaganjeAnalize ? 'Nalagam...' : 'Prikaži analizo'}
            </button>
          </form>

          {napakaAnalize && (
              <p className="analiza-napaka">{napakaAnalize}</p>
          )}

          {analiza !== null && (
              <div className="analiza-rezultat">
                <div className="analiza-vsota">
                  <span>Skupaj v izbranem obdobju</span>
                  <strong>{formatirajZnesek(Number(analiza.skupaj))}</strong>
                  <small>
                    {analiza.stroski.length} {analiza.stroski.length === 1
                      ? 'strošek'
                      : 'stroškov'}
                  </small>
                </div>

                {analiza.stroski.length === 0 ? (
                    <p className="prazno">
                      V izbranem obdobju ni zabeleženih stroškov.
                    </p>
                ) : (
                    <div className="tabela-ovoj analiza-tabela">
                      <table>
                        <thead>
                        <tr>
                          <th>Opis</th>
                          <th>Kategorija</th>
                          <th>Datum</th>
                          <th>Znesek</th>
                        </tr>
                        </thead>
                        <tbody>
                        {analiza.stroski.map((strosek) => (
                            <tr key={strosek.id}>
                              <td className="opis-celica">{strosek.opis}</td>
                              <td>
                    <span className="kategorija">
                      {strosek.kategorija}
                    </span>
                              </td>
                              <td>{strosek.datum}</td>
                              <td className="znesek-celica">
                                {formatirajZnesek(Number(strosek.znesek))}
                              </td>
                            </tr>
                        ))}
                        </tbody>
                      </table>
                    </div>
                )}
              </div>
          )}
        </section>

        <footer>
          Tim Kovačič - Aplikacija za sledenje lastnim stroškom
        </footer>
      </main>
  )
}

export default App
