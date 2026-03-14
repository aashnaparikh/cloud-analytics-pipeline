import clsx from 'clsx'

export default function LoadingSpinner({ className }: { className?: string }) {
  return (
    <div className={clsx('flex items-center justify-center', className)}>
      <div className="w-6 h-6 border-2 border-surface-border border-t-brand-500 rounded-full animate-spin" />
    </div>
  )
}
