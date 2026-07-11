import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ClassTypeService {
  readonly classTypeDictionary: Record<string, string> = {
    'individual': 'Individual',
    'grupo': 'Em Grupo',
    'revisao_intensiva': 'Revisão Intensiva',
    'apoio_tarefa': 'Apoio à Tarefa',
    'preparacao_prova': 'Preparação para Provas',
    'recuperacao_escolar': 'Recuperação Escolar',
    'oficina': 'Oficina',
    'online': 'Online'
  };

  getFriendlyClassTypeName(dbName: string): string {
    return this.classTypeDictionary[dbName] || dbName;
  }
}
