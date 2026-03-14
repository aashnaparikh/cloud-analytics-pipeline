import { api } from './api'
import type { AuthResponse } from '../types'

export const authService = {
  login: (email: string, password: string): Promise<AuthResponse> =>
    api.post('/auth/login', { email, password }).then((r) => r.data),

  register: (data: {
    email: string
    password: string
    firstName: string
    lastName: string
    tenantId: string
  }): Promise<AuthResponse> => api.post('/auth/register', data).then((r) => r.data),
}
