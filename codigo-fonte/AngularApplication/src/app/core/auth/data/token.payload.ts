export interface TokenPayload {
  scope: string | null | undefined;
  sub: string | null | undefined;
  exp: number | null | undefined;
}