import { useMotionPalette, spacing, radii } from '@native-motion/react-native';
export { spacing, radii };
export function useAppTheme() {
  const p = useMotionPalette();
  return { ...p, mutedSurface: p.background, ink: p.primaryText, muted: p.secondaryText,
    blueSoft: '#E7EEFD', borderSoft: p.border };
}
