import { File, Paths } from 'expo-file-system';
import * as DocumentPicker from 'expo-document-picker';
import * as Sharing from 'expo-sharing';
import { findPlan } from '../catalog/queries';
import { loadSnapshot, replaceFromBackup } from '../../lib/storage/database';
import { createBackup, parseBackup, MAX_BACKUP_BYTES, type BackupFile } from './backup-format';

export async function shareBackup(): Promise<void> {
  if (!(await Sharing.isAvailableAsync())) {
    throw new Error('Bu cihazda dosya paylaşımı kullanılamıyor.');
  }
  const snapshot = await loadSnapshot();
  const content = JSON.stringify(createBackup(snapshot), null, 2);
  const stamp = new Date().toISOString().slice(0, 10);
  const file = new File(Paths.cache, 'haftalik-plan-yedek-' + stamp + '.json');
  file.create({ overwrite: true });
  await file.write(content);
  await Sharing.shareAsync(file.uri, { mimeType: 'application/json', dialogTitle: 'Haftalık Plan yedeğini kaydet' });
}

export async function pickBackup(): Promise<BackupFile | null> {
  const result = await DocumentPicker.getDocumentAsync({
    type: ['application/json', 'text/plain', 'application/octet-stream'],
    copyToCacheDirectory: true, multiple: false
  });
  if (result.canceled) return null;
  const asset = result.assets[0];
  if (!asset) throw new Error('Dosya seçilemedi.');
  if (asset.size !== undefined && asset.size > MAX_BACKUP_BYTES) {
    throw new Error('Yedek dosyası 6 MB sınırını aşıyor.');
  }
  const file = new File(asset.uri);
  if (file.size > MAX_BACKUP_BYTES) throw new Error('Yedek dosyası 6 MB sınırını aşıyor.');
  return parseBackup(await file.text(), findPlan);
}

export async function restoreBackup(backup: BackupFile): Promise<void> {
  await replaceFromBackup(backup);
}
