import { useEffect, useState } from 'react'

export default function AuthScreen({ onLogin, onRegister, busy, authError, authNotice }) {
  const [mode, setMode] = useState('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')

  const isLogin = mode === 'login'

  // Onay bekleyen bir kayıttan sonra kullanıcıyı giriş sekmesine geri al.
  useEffect(() => {
    if (authNotice) setMode('login')
  }, [authNotice])

  function handleSubmit(e) {
    e.preventDefault()
    if (isLogin) onLogin(email, password)
    else onRegister(email, password, displayName)
  }

  return (
    <div
      className="fixed inset-0 flex items-center justify-center"
      style={{ background: 'var(--ui-bg)', color: 'var(--ui-text)', fontFamily: 'var(--font-body)' }}
    >
      <form onSubmit={handleSubmit} className="w-[400px] flex flex-col gap-5">
        <div className="font-heading font-extrabold tracking-[0.08em] text-2xl">
          NOTA<span style={{ color: 'var(--color-accent)' }}>.</span>
        </div>

        <div className="flex border-2 rounded-[var(--ui-radius)] overflow-hidden" style={{ borderColor: 'var(--ui-line)' }}>
          {[{ m: 'login', label: 'Giriş Yap' }, { m: 'signup', label: 'Kayıt Ol' }].map((o) => (
            <button
              key={o.m}
              type="button"
              onClick={() => setMode(o.m)}
              className="flex-1 py-2.5 text-[13px] font-bold"
              style={{ background: mode === o.m ? 'var(--ui-text)' : 'transparent', color: mode === o.m ? 'var(--ui-bg)' : 'var(--ui-muted)' }}
            >
              {o.label}
            </button>
          ))}
        </div>

        <div className="flex flex-col gap-3">
          {!isLogin && (
            <div className="flex flex-col gap-1.5">
              <label className="text-[11px] font-extrabold tracking-[0.08em] uppercase" style={{ color: 'var(--ui-muted)' }}>Ad Soyad</label>
              <input className="input text-sm h-10" placeholder="Deniz Yılmaz" value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
            </div>
          )}
          <div className="flex flex-col gap-1.5">
            <label className="text-[11px] font-extrabold tracking-[0.08em] uppercase" style={{ color: 'var(--ui-muted)' }}>E-posta</label>
            <input
              className="input text-sm h-10" type="email" placeholder="ornek@mail.com" required
              value={email} onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="flex flex-col gap-1.5">
            <label className="text-[11px] font-extrabold tracking-[0.08em] uppercase" style={{ color: 'var(--ui-muted)' }}>Şifre</label>
            <input
              className="input text-sm h-10" type="password" placeholder="••••••••" required minLength={8}
              value={password} onChange={(e) => setPassword(e.target.value)}
            />
          </div>
        </div>

        {authNotice && (
          <div className="text-[13px] px-3 py-2 rounded-[var(--ui-radius)]" style={{ background: 'var(--ui-hover)', color: 'var(--ui-text)' }}>
            {authNotice}
          </div>
        )}
        {authError && (
          <div className="text-[13px] px-3 py-2 rounded-[var(--ui-radius)]" style={{ background: 'var(--ui-accent-soft)', color: 'var(--ui-text)' }}>
            {authError}
          </div>
        )}

        <button type="submit" disabled={busy} className="btn btn-primary btn-block justify-center text-sm h-[42px]">
          {busy ? 'Lütfen bekleyin…' : isLogin ? 'Giriş Yap' : 'Hesap Oluştur'}
        </button>

        {!isLogin && (
          <div className="text-[13px]" style={{ color: 'var(--ui-muted)' }}>
            Kayıt olarak notlarının bu hesaba özel olacağını kabul etmiş olursun.
          </div>
        )}
      </form>
    </div>
  )
}
