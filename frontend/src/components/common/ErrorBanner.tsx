import { AlertTriangle } from 'lucide-react'

export default function ErrorBanner({ message }: { message: string }) {
  return (
    <div className="flex items-center gap-3 p-4 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm">
      <AlertTriangle className="w-4 h-4 shrink-0" />
      <span>{message}</span>
    </div>
  )
}
